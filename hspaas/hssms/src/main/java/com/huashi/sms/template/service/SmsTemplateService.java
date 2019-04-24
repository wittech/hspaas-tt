package com.huashi.sms.template.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.user.service.IUserSmsConfigService;
import com.huashi.common.util.PatternUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.huashi.sms.template.context.SmsTemplateContext.ApproveStatus;
import com.huashi.sms.template.dao.MessageTemplateMapper;
import com.huashi.sms.template.domain.MessageTemplate;

/**
 * 短信模板服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年8月30日 下午2:08:14
 */
@Service
public class SmsTemplateService implements ISmsTemplateService {

    @Reference
    private IUserService                                       userService;
    @Autowired
    private MessageTemplateMapper                              messageTemplateMapper;
    @Reference
    private IUserSmsConfigService                              userSmsConfigService;
    @Autowired
    private IForbiddenWordsService                             forbiddenWordsService;
    @Resource
    private StringRedisTemplate                                stringRedisTemplate;

    private final Logger                                       logger                  = LoggerFactory.getLogger(getClass());

    /**
     * 全局短信模板（与REDIS 同步采用广播模式）
     */
    public static volatile Map<Integer, List<MessageTemplate>> GLOBAL_MESSAGE_TEMPLATE = new ConcurrentHashMap<>();

    /**
     * 超级模板正则表达式（一般指无限制）
     */
    private static final String                                SUPER_TEMPLATE_REGEX    = "^[\\s\\S]*$";

    @Override
    public PaginationVo<MessageTemplate> findPage(int userId, String status, String content, String currentPage) {
        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (StringUtils.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtils.isNotEmpty(content)) {
            params.put("content", content);
        }

        // WEB只能看到自己提交的数据
        params.put("appType", AppType.WEB.getCode());

        int totalRecord = messageTemplateMapper.getCountByUserId(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("startPage", PaginationVo.getStartPage(_currentPage));
        params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<MessageTemplate> list = messageTemplateMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    private String getKey(int userId) {
        return String.format("%s:%d", SmsRedisConstant.RED_USER_MESSAGE_TEMPLATE, userId);
    }

    /**
     * 添加到REDIS
     *
     * @param userId 用户编号
     * @param templates 短信模板数据
     */
    private void pushToRedis(int userId, MessageTemplate... templates) {
        try {

            stringRedisTemplate.execute((connection) -> {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();
                byte[] key = serializer.serialize(getKey(userId));

                for (MessageTemplate template : templates) {
                    connection.zAdd(key, template.getPriority().doubleValue(), JSON.toJSONBytes(template));

                    // publish message for flush jvm map data
                    connection.publish(serializer.serialize(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC),
                                       JSON.toJSONBytes(template));
                }

                return connection.closePipeline();
            }, false, true);

        } catch (Exception e) {
            logger.warn("REDIS 短信模板设置失败", e);
        }
    }

    /**
     * 根据用户编号查询短信模板集合信息
     * 
     * @param userId 用户ID
     * @return 模板集合数据
     */
    private List<MessageTemplate> getFromRedis(int userId) {
        try {
            Set<String> set = stringRedisTemplate.opsForZSet().reverseRangeByScore(getKey(userId), 0, 1000);
            if (CollectionUtils.isEmpty(set)) {
                return null;
            }

            List<MessageTemplate> list = new ArrayList<>(set.size());
            for (String s : set) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }

                list.add(JSON.parseObject(s, MessageTemplate.class));
            }

            return list;
        } catch (Exception e) {
            logger.warn("REDIS 短信模板获取失败", e);
            return null;
        }
    }

    /**
     * 从redis和内存中移除模板数据
     * 
     * @param template 模板信息
     */
    private void removeRedis(MessageTemplate template) {
        try {
            stringRedisTemplate.execute((connection) -> {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();

                byte[] key = serializer.serialize(getKey(template.getUserId()));
                Set<byte[]> templates = connection.zRevRangeByScore(key, 0, 1000);
                if (CollectionUtils.isEmpty(templates)) {
                    logger.warn("redis中未找到短信模板 userId[" + template.getUserId() + "]信息");
                    return null;
                }

                for (byte[] templateJson : templates) {
                    MessageTemplate messageTemplate = JSON.parseObject(templateJson, MessageTemplate.class);
                    if (messageTemplate == null) {
                        continue;
                    }

                    if (template.getContent().equals(messageTemplate.getContent())) {
                        connection.zRem(key, templateJson);
                        // 发布订阅频道消息
                        connection.publish(serializer.serialize(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC),
                                           templateJson);
                        break;
                    }
                }

                return connection.closePipeline();

            }, false, true);

        } catch (Exception e) {
            logger.warn("REDIS 短信模板移除失败", e);
        }
    }

    @Override
    public boolean update(MessageTemplate template) {
        MessageTemplate originTemplate;
        try {
            originTemplate = isAllowAccess(template.getUserId(), template.getId());
        } catch (IllegalArgumentException e) {
            logger.error("模板数据鉴权失败 : {}", e.getMessage());
            return false;
        }

        template.setRegexValue(parseContent2Regex(template.getContent()));
        template.setCreateTime(originTemplate.getCreateTime());
        int result = messageTemplateMapper.updateByPrimaryKey(template);
        if (result > 0) {
            updateTemplateInRedis(template);
        }
        return result > 0;
    }

    /**
     * 是否允许被访问（针对用户ID进行鉴权,防止恶意使用userID来篡改其他userId数据）
     * 
     * @param userId 用户ID
     * @param templateId 模板ID
     * @return 模板信息
     */
    private MessageTemplate isAllowAccess(int userId, long templateId) {
        MessageTemplate template = get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板 [" + templateId + "]信息为空");
        }

        // 仅针对WEB用户自己添加的模板进行过滤
        if (AppType.WEB.getCode() == template.getAppType() && template.getUserId() != userId) {
            throw new IllegalArgumentException("用户模板[" + templateId + "]数据不匹配，原模板用户ID:[" + template.getUserId()
                                               + "] , 本次用户ID:[" + userId + "]");
        }

        if (AppType.WEB.getCode() == template.getAppType()
            && template.getStatus() != ApproveStatus.WAITING.getValue()) {
            throw new IllegalArgumentException("用户模板[" + templateId + "]模板状态为非待审核状态[" + template.getStatus() + "]不能修改");
        }

        return template;
    }

    @Override
    public boolean deleteById(long id) {
        MessageTemplate template = get(id);
        if (template == null) {
            logger.error("用户短信模板为空，删除失败， ID：{}", id);
            return false;
        }

        try {
            removeRedis(template);
        } catch (Exception e) {
            logger.error("移除REDIS用户模板失败， ID：{}", id, e);
        }

        return messageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public MessageTemplate get(long id) {
        return messageTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public BossPaginationVo<MessageTemplate> findPageBoos(int pageNum, String keyword, String status, String userId) {
        BossPaginationVo<MessageTemplate> page = new BossPaginationVo<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("status", status);
        paramMap.put("userId", userId);
        int count = messageTemplateMapper.findCount(paramMap);
        page.setCurrentPage(pageNum);
        page.setTotalCount(count);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());

        List<MessageTemplate> list = messageTemplateMapper.findList(paramMap);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (MessageTemplate t : list) {
            if (t == null) {
                continue;
            }

            t.setUserModel(userService.getByUserId(t.getUserId()));
            t.setApptypeText(AppType.parse(t.getAppType()).getName());
            t.setRouteTypeText(RouteType.parse(t.getRouteType()).getName());
        }
        page.setList(list);
        return page;
    }

    /**
     * 根据内容匹配最符合条件的的短信模板数据（按照优先级排序选择，按照有精确模板优先，超级模板次之）
     * 
     * @param list 短信模板集合
     * @param content 短信内容
     * @return 匹配后的模板数据
     */
    private MessageTemplate matchAccessTemplate(List<MessageTemplate> list, String content) {
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("There is no message template result matched");
            return null;
        }

        MessageTemplate superTemplate = null;
        for (MessageTemplate template : list) {
            if (template == null) {
                logger.warn("LOOP当前值为空");
                continue;
            }

            if (StringUtils.isEmpty(template.getRegexValue())) {
                continue;
            }

            if (SUPER_TEMPLATE_REGEX.equalsIgnoreCase(template.getRegexValue())) {
                superTemplate = template;
                continue;
            }

            // 如果普通短信模板存在，则以普通模板为主
            if (PatternUtil.isRight(template.getRegexValue(), content)) {
                return template;
            }
        }

        // 如果普通短信模板未找到，判断是否找到超级模板，有则直接返回
        if (superTemplate != null) {
            return superTemplate;
        }

        return null;
    }

    @Override
    public MessageTemplate getByContent(int userId, String content) {
        List<MessageTemplate> list = getTemplatesByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return matchAccessTemplate(list, content);
    }

    /**
     * 根据用户ID获取该用户所有的短信模板数据
     *
     * @param userId 用户ID
     * @return 模板集合
     */
    private List<MessageTemplate> getTemplatesByUserId(int userId) {
        if (CollectionUtils.isNotEmpty(GLOBAL_MESSAGE_TEMPLATE.get(userId))) {
            return GLOBAL_MESSAGE_TEMPLATE.get(userId);
        }

        List<MessageTemplate> list = null;
        try {
            list = getFromRedis(userId);
            if (CollectionUtils.isNotEmpty(list)) {
                return list;
            }

            list = messageTemplateMapper.findAvaiableByUserId(userId);
            if (CollectionUtils.isNotEmpty(list)) {
                // 如果DB中存在，REDIS中不存在，则需要加载至REDIS
                pushToRedis(userId, list.toArray(new MessageTemplate[] {}));
            }

            return list;
        } catch (Exception e) {
            logger.warn("No templates found by userId [" + userId + "]", e);
            return null;
        } finally {
            if (CollectionUtils.isNotEmpty(list)) {
                GLOBAL_MESSAGE_TEMPLATE.put(userId, list);
            }
        }
    }

    /**
     * 设置常规属性数据
     * 
     * @param template 模板信息
     */
    private void setProperties(MessageTemplate template) {
        template.setCreateTime(new Date());
        // 融合平台判断 后台添加 状态默认
        if (AppType.WEB.getCode() == template.getAppType()) {
            template.setStatus(ApproveStatus.WAITING.getValue());
        }
        template.setRegexValue(parseContent2Regex(template.getContent()));

        if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            pushToRedis(template.getUserId(), template);
        }
    }

    @Override
    public boolean save(MessageTemplate template) {
        if (StringUtils.isEmpty(template.getContent())) {
            throw new IllegalArgumentException("模板内容不能为空");
        }

        Set<String> words = forbiddenWordsService.filterForbiddenWords(template.getContent());
        if (CollectionUtils.isNotEmpty(words)) {
            throw new IllegalArgumentException("模板内容包含敏感词[" + words + "]");
        }

        setProperties(template);

        return messageTemplateMapper.insertSelective(template) > 0;
    }

    @Override
    @Transactional
    public boolean saveToBatchContent(MessageTemplate template, String[] contents) {
        Set<String> words = forbiddenWordsService.filterForbiddenWords(template.getContent());
        if (CollectionUtils.isNotEmpty(words)) {
            throw new IllegalArgumentException("模板内容包含敏感词[" + words + "]");
        }

        if (AppType.WEB.getCode() == template.getAppType()) {
            template.setStatus(ApproveStatus.WAITING.getValue());
        }

        List<MessageTemplate> list = new ArrayList<>();
        for (String content : contents) {
            MessageTemplate newTemplate = new MessageTemplate();
            BeanUtils.copyProperties(template, newTemplate);

            newTemplate.setContent(content);
            newTemplate.setCreateTime(new Date());
            newTemplate.setRegexValue(parseContent2Regex(content));

            list.add(newTemplate);
        }

        if (CollectionUtils.isEmpty(list)) {
            logger.warn("No template data need to process by contents [" + StringUtils.join(contents) + "]");
            return false;
        }

        int result = messageTemplateMapper.batchInsert(list);
        if (result <= 0) {
            logger.error("Template contents[" + StringUtils.join(contents) + "] persist process failed");
            return false;
        }

        if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            pushToRedis(template.getUserId(), list.toArray(new MessageTemplate[] {}));
        }

        return true;
    }

    /**
     * 内容转换正则表达式
     * 
     * @param content 短信内容
     * @return 转换后的内容
     */
    private static String parseContent2Regex(String content) {
        // modify 变量内容 增加不可见字符
        content = content.replaceAll("#[a-z]*[0-9]*[A-Z]*#", "[\\\\s\\\\S]*").replaceAll("\\{[a-z]*[0-9]*[A-Z]*\\}",
                                                                                         "[\\\\s\\\\S]*");
        // 去掉末尾可以增加空格等不可见字符，以免提供商模板不通过
        // return prefix+oriStr+"\\s*$";
        return String.format("^%s$", content);

    }

    @Override
    public boolean approve(long id, int status, String remark) {
        MessageTemplate template = get(id);
        template.setStatus(status);
        template.setRemark(remark);
        template.setApproveTime(new Date());

        if (ApproveStatus.SUCCESS.getValue() == status) {
            pushToRedis(template.getUserId(), template);
        } else {
            removeRedis(template);
        }

        return messageTemplateMapper.updateByPrimaryKeySelective(template) > 0;
    }

    @Override
    public boolean isContentMatched(long id, String content) {
        MessageTemplate template = get(id);
        return template != null && PatternUtil.isRight(template.getRegexValue(), content);
    }

    /**
     * 跟新缓存中模板数据
     * 
     * @param template 模板数据
     */
    private void updateTemplateInRedis(MessageTemplate template) {
        final MessageTemplate originTemplate = messageTemplateMapper.selectByPrimaryKey(template.getId());
        try {
            stringRedisTemplate.execute((connection) -> {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();
                byte[] key = serializer.serialize(getKey(template.getUserId()));

                if (originTemplate != null) {
                    // 删除原有的模板数据
                    connection.zRem(key, JSON.toJSONBytes(originTemplate));
                }

                connection.zAdd(key, template.getPriority().doubleValue(), JSON.toJSONBytes(template));

                // publish message for flush jvm map data
                connection.publish(serializer.serialize(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC),
                                   JSON.toJSONBytes(template));

                return connection.closePipeline();
            }, false, true);

        } catch (Exception e) {
            logger.warn("REDIS 短信模板[" + JSON.toJSONString(template) + "]加载失败", e);
        }
    }

    @Override
    public boolean reloadToRedis() {
        List<MessageTemplate> list = messageTemplateMapper.findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("短信模板数据为空");
            return false;
        }

        List<Object> con = stringRedisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
            connection.openPipeline();

            // 查询该用户编号下的所有模板信息
            Set<byte[]> keys = connection.keys(serializer.serialize(SmsRedisConstant.RED_USER_MESSAGE_TEMPLATE + "*"));
            if (CollectionUtils.isNotEmpty(keys)) {
                connection.del(keys.toArray(new byte[][] {}));
            }

            for (MessageTemplate template : list) {
                byte[] key = serializer.serialize(getKey(template.getUserId()));

                connection.zAdd(key, template.getPriority().doubleValue(),JSON.toJSONBytes(template));
            }

            return connection.closePipeline();

        }, false, true);

        return CollectionUtils.isNotEmpty(con);
    }

    @Override
    public boolean delete(long id, int userId) {
        MessageTemplate originTemplate;
        try {
            originTemplate = isAllowAccess(userId, id);
        } catch (IllegalArgumentException e) {
            logger.error("模板数据鉴权失败 : {}", e.getMessage());
            return false;
        }

        try {
            removeRedis(originTemplate);
        } catch (Exception e) {
            logger.error("移除REDIS用户模板失败， ID：{}", id, e);
        }

        return messageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

}
