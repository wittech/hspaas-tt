package com.huashi.sms.template.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.huashi.sms.template.context.TemplateContext.ApproveStatus;
import com.huashi.sms.template.dao.MessageTemplateMapper;
import com.huashi.sms.template.domain.MessageTemplate;

/**
 * TODO 短信模板服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年8月30日 下午2:08:14
 */
@Service
public class SmsTemplateService implements ISmsTemplateService {

    @Reference
    private IUserService                             userService;
    @Autowired
    private MessageTemplateMapper                    messageTemplateMapper;
    @Reference
    private IUserSmsConfigService                    userSmsConfigService;
    @Autowired
    private IForbiddenWordsService                   forbiddenWordsService;
    @Resource
    private StringRedisTemplate                      stringRedisTemplate;
    private final Logger                             logger                  = LoggerFactory.getLogger(getClass());

    /**
     * 全局短信模板（与REDIS 同步采用广播模式）
     */
    public static volatile Map<Integer, Set<String>> GLOBAL_MESSAGE_TEMPLATE = new ConcurrentHashMap<>();

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
     * TODO 添加到REDIS
     * 
     * @param template
     * @param userId
     */
    private void pushToRedis(int userId, MessageTemplate template) {
        try {
            stringRedisTemplate.opsForZSet().add(getKey(userId), JSON.toJSONString(template),
                                                 template.getPriority().doubleValue());

            // 订阅发布模式订阅
            stringRedisTemplate.convertAndSend(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC, "all");
        } catch (Exception e) {
            logger.warn("REDIS 短信模板设置失败", e);
        }
    }

    /**
     * TODO 查询REDIS
     * 
     * @param userId
     */
    private Set<String> getFromRedis(int userId) {
        try {

            if (MapUtils.isEmpty(GLOBAL_MESSAGE_TEMPLATE)
                || CollectionUtils.isEmpty(GLOBAL_MESSAGE_TEMPLATE.get(userId))) {
                Set<String> set = stringRedisTemplate.opsForZSet().reverseRangeByScore(getKey(userId), 0, 1000);
                if (CollectionUtils.isEmpty(set)) {
                    return null;
                }

                GLOBAL_MESSAGE_TEMPLATE.put(userId, set);
            }

            return GLOBAL_MESSAGE_TEMPLATE.get(userId);

        } catch (Exception e) {
            logger.warn("REDIS 短信模板设置失败", e);
            return null;
        }
    }

    private void removeRedis(MessageTemplate template) {
        try {
            Set<String> set = stringRedisTemplate.opsForZSet().reverseRangeByScore(getKey(template.getUserId()), 0,
                                                                                   template.getPriority());
            if (CollectionUtils.isEmpty(set)) {
                logger.info("未找到短信模板信息:{}", template.getUserId());
                return;
            }

            for (String s : set) {
                MessageTemplate jt = JSON.parseObject(s, MessageTemplate.class);
                if (jt == null) {
                    continue;
                }

                if (template.getContent().equals(jt.getContent())) {
                    stringRedisTemplate.opsForZSet().remove(getKey(template.getUserId()), s);
                }
            }

            // 订阅发布模式订阅
            stringRedisTemplate.convertAndSend(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC, "all");

        } catch (Exception e) {
            logger.warn("REDIS 短信模板移除失败", e);
        }
    }

    @Override
    public boolean update(MessageTemplate template) {
        MessageTemplate originTemplate = null;
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
            reloadUserTemplate(template.getUserId());
        }
        return result > 0;
    }

    /**
     * TODO 是否允许被访问（针对用户ID进行鉴权,防止恶意使用userID来篡改其他userId数据）
     * 
     * @param userId
     * @param templateId
     * @return
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
     * TODO 由REDIS查询短信模板
     * 
     * @param userId
     * @param content
     * @return
     */
    private MessageTemplate getTemplateFromRedis(int userId, String content) {
        try {
            // 超级模板表达式
            String superTemplateRegex = "^[\\s\\S]*$";
            MessageTemplate superTemplate = null;
            Set<String> texts = getFromRedis(userId);
            if (CollectionUtils.isNotEmpty(texts)) {
                for (String text : texts) {
                    MessageTemplate template = JSON.parseObject(text, MessageTemplate.class);
                    if (template == null) {
                        logger.warn("LOOP当前值为空");
                        continue;
                    }

                    if (StringUtils.isEmpty(template.getRegexValue())) {
                        continue;
                    }

                    if (superTemplateRegex.equalsIgnoreCase(template.getRegexValue())) {
                        superTemplate = template;
                        continue;
                    }

                    // 如果普通短信模板存在，则以普通模板为主
                    if (PatternUtil.isRight(template.getRegexValue(), content)) {
                        return template;
                    }

                }
            }

            // 如果普通短信模板未找到，判断是否找到超级模板，有则直接返回
            if (superTemplate != null) {
                return superTemplate;
            }
        } catch (Exception e) {
            logger.error("短信模板REDIS查询失败", e);
        }
        return null;
    }

    private MessageTemplate getTemplateFromDb(int userId, String content) {
        // REDIS没查到
        // MessageTemplate superTemplate = null;
        List<MessageTemplate> list = messageTemplateMapper.findAvaiableByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (MessageTemplate template : list) {
            if (StringUtils.isEmpty(template.getRegexValue())) {
                continue;
            }

            // if (TEMPLATE_SUPER_REGEX.equalsIgnoreCase(template.getRegexValue())) {
            // superTemplate = template;
            // continue;
            // }

            // 如果普通短信模板存在，则以普通模板为主
            if (PatternUtil.isRight(template.getRegexValue(), content)) {
                return template;
            }
        }

        // 如果普通短信模板未找到，判断是否找到超级模板，有则直接返回
        // if (superTemplate != null)
        // return superTemplate;

        return null;
    }

    @Override
    public MessageTemplate getByContent(int userId, String content) {
        MessageTemplate template = getTemplateFromRedis(userId, content);
        if (template != null) {
            return template;
        }

        return getTemplateFromDb(userId, content);
    }

    @Override
    public boolean save(MessageTemplate template) {
        if (StringUtils.isEmpty(template.getContent())) {
            throw new RuntimeException("模板内容不能为空");
        }

        Set<String> words = forbiddenWordsService.filterForbiddenWords(template.getContent());
        if (CollectionUtils.isNotEmpty(words)) {
            throw new RuntimeException(String.format("模板内容包含敏感词：%s", words));
        }
        template.setCreateTime(new Date());
        // 融合平台判断 后台添加 状态默认
        if (AppType.WEB.getCode() == template.getAppType()) {
            template.setStatus(ApproveStatus.WAITING.getValue());
        }
        template.setRegexValue(parseContent2Regex(template.getContent()));

        if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            pushToRedis(template.getUserId(), template);
        }

        return messageTemplateMapper.insertSelective(template) > 0;
    }

    @Override
    @Transactional
    public boolean saveToBatchContent(MessageTemplate template, String[] contents) {
        Set<String> words = forbiddenWordsService.filterForbiddenWords(template.getContent());
        if (CollectionUtils.isNotEmpty(words)) {
            throw new RuntimeException(String.format("模板内容包含敏感词：%s", words));
        }

        if (AppType.WEB.getCode() == template.getAppType()) {
            template.setStatus(ApproveStatus.WAITING.getValue());
        }

        Set<String> set = new HashSet<>();
        CollectionUtils.addAll(set, contents);
        boolean allResult = true;
        for (String content : set) {
            template.setId(null);
            template.setContent(content);
            template.setCreateTime(new Date());
            template.setRegexValue(parseContent2Regex(content));

            int result = messageTemplateMapper.insertSelective(template);
            if (result <= 0) {
                allResult = false;
                break;
            }

            if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
                pushToRedis(template.getUserId(), template);
            }
        }
        if (!allResult) {
            // 非全部成功的，全部回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return allResult;
    }

    /**
     * TODO 内容转换正则表达式
     * 
     * @param content
     * @return
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
    public boolean approve(long id, int approveStatus, String operator) {
        MessageTemplate template = get(id);
        template.setStatus(approveStatus);
        template.setApproveUser(operator);
        template.setApproveTime(new Date());

        if (ApproveStatus.SUCCESS.getValue() == approveStatus) {
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

    private void reloadUserTemplate(int userId) {
        List<MessageTemplate> list = messageTemplateMapper.findAvaiableByUserId(userId);
        stringRedisTemplate.delete(getKey(userId));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (MessageTemplate t : list) {
            stringRedisTemplate.opsForZSet().add(getKey(userId), JSON.toJSONString(t), t.getPriority().doubleValue());
        }

        // 订阅发布模式订阅
        stringRedisTemplate.convertAndSend(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC, "all");
    }

    @Override
    public boolean reloadToRedis() {
        List<MessageTemplate> list = messageTemplateMapper.findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("短信模板数据为空");
            return false;
        }

        stringRedisTemplate.delete(stringRedisTemplate.keys(SmsRedisConstant.RED_USER_MESSAGE_TEMPLATE + "*"));

        for (MessageTemplate template : list) {
            try {
                stringRedisTemplate.opsForZSet().add(getKey(template.getUserId()), JSON.toJSONString(template),
                                                     template.getPriority().doubleValue());

            } catch (Exception e) {
                logger.warn("REDIS 短信模板设置失败", e);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean delete(long id, int userId) {
        MessageTemplate originTemplate = null;
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
