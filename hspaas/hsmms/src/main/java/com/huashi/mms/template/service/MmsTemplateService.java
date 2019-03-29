package com.huashi.mms.template.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.user.service.IUserMmsConfigService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.mms.config.cache.redis.constant.MmsRedisConstant;
import com.huashi.mms.template.constant.MediaFileDirectory;
import com.huashi.mms.template.constant.MmsTemplateContext.ApproveStatus;
import com.huashi.mms.template.constant.MmsTemplateContext.MediaType;
import com.huashi.mms.template.dao.MmsMessageTemplateMapper;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.mms.template.exception.BodyCheckException;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 彩信模板服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月20日 下午10:06:27
 */
@Service
public class MmsTemplateService implements IMmsTemplateService {

    @Reference
    private IUserService                             userService;
    @Reference
    private IUserMmsConfigService                    userMmsConfigService;
    @Resource
    private StringRedisTemplate                      stringRedisTemplate;
    @Autowired
    private MmsMediaFileService                      mmsMediaFileService;
    @Autowired
    private MmsMessageTemplateMapper                 mmsMessageTemplateMapper;

    /**
     * 多媒体类型
     */
    private static final String                      NODE_MEDIA_TYPE         = "mediaType";

    /**
     * 多媒体文件名称
     */
    private static final String                      NODE_MEDIA_NAME         = "mediaName";

    /**
     * 多媒体内容
     */
    private static final String                      NODE_CONTENT             = "content";

    private final Logger                             logger                  = LoggerFactory.getLogger(getClass());

    /**
     * 全局彩信模板（与REDIS 同步采用广播模式）
     */
    public static volatile Map<Integer, Set<String>> GLOBAL_MESSAGE_TEMPLATE = new ConcurrentHashMap<>();

    @Override
    public PaginationVo<MmsMessageTemplate> findPage(int userId, String status, String title, String currentPage) {
        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (StringUtils.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtils.isNotEmpty(title)) {
            params.put("content", title);
        }

        // WEB只能看到自己提交的数据
        params.put("appType", AppType.WEB.getCode());

        int totalRecord = mmsMessageTemplateMapper.getCountByUserId(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("startPage", PaginationVo.getStartPage(_currentPage));
        params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    private String getKey(int userId) {
        return String.format("%s:%d", MmsRedisConstant.RED_USER_MESSAGE_TEMPLATE, userId);
    }

    /**
     * 添加到REDIS
     *
     * @param userId
     * @param template
     */
    private void pushToRedis(int userId, MmsMessageTemplate template) {
        try {
            stringRedisTemplate.opsForZSet().add(getKey(userId), JSON.toJSONString(template),
                                                 template.getPriority().doubleValue());

            // 订阅发布模式订阅
            stringRedisTemplate.convertAndSend(MmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC, "all");
        } catch (Exception e) {
            logger.warn("REDIS 彩信模板设置失败", e);
        }
    }

    /**
     * 查询REDIS
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
            logger.warn("REDIS 彩信模板设置失败", e);
            return null;
        }
    }

    private void removeRedis(MmsMessageTemplate template) {
        try {
            Set<String> set = stringRedisTemplate.opsForZSet().reverseRangeByScore(getKey(template.getUserId()), 0,
                                                                                   template.getPriority());
            if (CollectionUtils.isEmpty(set)) {
                logger.info("未找到彩信模板信息:{}", template.getUserId());
                return;
            }

            for (String s : set) {
                MmsMessageTemplate jt = JSON.parseObject(s, MmsMessageTemplate.class);
                if (jt == null) {
                    continue;
                }

                // if (template.getContent().equals(jt.getContent())) {
                // stringRedisTemplate.opsForZSet().remove(getKey(template.getUserId()), s);
                // }
            }

            // 订阅发布模式订阅
            stringRedisTemplate.convertAndSend(MmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC, "all");

        } catch (Exception e) {
            logger.warn("REDIS 彩信模板移除失败", e);
        }
    }

    @Override
    public boolean update(MmsMessageTemplate template) {
        MmsMessageTemplate originTemplate = null;
        try {
            originTemplate = isAllowAccess(template.getUserId(), template.getId());
        } catch (IllegalArgumentException e) {
            logger.error("模板数据鉴权失败 : {}", e.getMessage());
            return false;
        }

        // template.setRegexValue(parseContent2Regex(template.getContent()));
        template.setCreateTime(originTemplate.getCreateTime());
        int result = mmsMessageTemplateMapper.updateByPrimaryKey(template);
        if (result > 0) {
            reloadUserTemplate(template.getUserId());
        }
        return result > 0;
    }

    /**
     * 是否允许被访问（针对用户ID进行鉴权,防止恶意使用userID来篡改其他userId数据）
     * 
     * @param userId
     * @param templateId
     * @return
     */
    private MmsMessageTemplate isAllowAccess(int userId, long templateId) {
        MmsMessageTemplate template = get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板 [" + templateId + "]信息为空");
        }

        // 仅针对WEB用户自己添加的模板进行过滤
        if (AppType.WEB.getCode() == template.getAppType() && template.getUserId() != userId) {
            throw new IllegalArgumentException("用户模板[" + templateId + "]数据不匹配，原模板用户ID:[" + template.getUserId()
                                               + "] , 本次用户ID:[" + userId + "]");
        }

        if (AppType.WEB.getCode() == template.getAppType() && template.getStatus() != ApproveStatus.WAITING.getValue()) {
            throw new IllegalArgumentException("用户模板[" + templateId + "]模板状态为非待审核状态[" + template.getStatus() + "]不能修改");
        }

        return template;
    }

    @Override
    public boolean deleteById(long id) {
        MmsMessageTemplate template = get(id);
        if (template == null) {
            logger.error("用户彩信模板为空，删除失败， ID：{}", id);
            return false;
        }

        try {
            removeRedis(template);
        } catch (Exception e) {
            logger.error("移除REDIS用户模板失败， ID：{}", id, e);
        }

        return mmsMessageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public MmsMessageTemplate get(long id) {
        return mmsMessageTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public BossPaginationVo<MmsMessageTemplate> findPageBoos(int pageNum, String keyword, String status, String userId) {
        BossPaginationVo<MmsMessageTemplate> page = new BossPaginationVo<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("status", status);
        paramMap.put("userId", userId);
        int count = mmsMessageTemplateMapper.findCount(paramMap);
        page.setCurrentPage(pageNum);
        page.setTotalCount(count);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());

        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findList(paramMap);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (MmsMessageTemplate t : list) {
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
     * 由REDIS查询彩信模板
     * 
     * @param userId
     * @param content
     * @return
     */
    private MmsMessageTemplate getTemplateFromRedis(int userId, String content) {
        try {
            // 超级模板表达式
            String superTemplateRegex = "^[\\s\\S]*$";
            MmsMessageTemplate superTemplate = null;
            Set<String> texts = getFromRedis(userId);
            if (CollectionUtils.isNotEmpty(texts)) {
                for (String text : texts) {
                    MmsMessageTemplate template = JSON.parseObject(text, MmsMessageTemplate.class);
                    if (template == null) {
                        logger.warn("LOOP当前值为空");
                        continue;
                    }

                }
            }

            // 如果普通彩信模板未找到，判断是否找到超级模板，有则直接返回
            if (superTemplate != null) {
                return superTemplate;
            }
        } catch (Exception e) {
            logger.error("彩信模板REDIS查询失败", e);
        }
        return null;
    }

    private MmsMessageTemplate getTemplateFromDb(int userId, String content) {
        // REDIS没查到
        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findAvaiableByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (MmsMessageTemplate template : list) {
            // if (StringUtils.isEmpty(template.getRegexValue())) {
            // continue;
            // }

            // 如果普通彩信模板存在，则以普通模板为主
            // if (PatternUtil.isRight(template.getRegexValue(), content)) {
            // return template;
            // }
        }

        // 如果普通彩信模板未找到，判断是否找到超级模板，有则直接返回
        // if (superTemplate != null)
        // return superTemplate;

        return null;
    }

    @Override
    public MmsMessageTemplate getByContent(int userId, String content) {
        MmsMessageTemplate template = getTemplateFromRedis(userId, content);
        if (template != null) {
            return template;
        }

        return getTemplateFromDb(userId, content);
    }

    @Override
    public boolean save(MmsMessageTemplate template) {
        template.setCreateTime(new Date());
        // 融合平台判断 后台添加 状态默认
        if (AppType.WEB.getCode() == template.getAppType()) {
            template.setStatus(ApproveStatus.WAITING.getValue());
        }
        if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            pushToRedis(template.getUserId(), template);
        }

        return mmsMessageTemplateMapper.insertSelective(template) > 0;
    }

    
    private boolean dealMessageTemplate(MmsMessageTemplate template) {
        try {
            List<MmsMessageTemplateBody> bodies = template.getBodies();
            if(CollectionUtils.isEmpty(bodies)) {
                throw new IllegalArgumentException("模板内容结构体数据为空");
            }
            
            
            
            
            
        } catch (Exception e) {
        }
        
        
        return true;
    }

    @Override
    public boolean approve(long id, int status, String remark) {
        MmsMessageTemplate template = get(id);
        template.setStatus(status);
        template.setRemark(remark);
        template.setApproveTime(new Date());

        if (ApproveStatus.SUCCESS.getValue() == status) {
            pushToRedis(template.getUserId(), template);
        } else {
            removeRedis(template);
        }

        return mmsMessageTemplateMapper.updateByPrimaryKeySelective(template) > 0;
    }

    private void reloadUserTemplate(int userId) {
        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findAvaiableByUserId(userId);
        stringRedisTemplate.delete(getKey(userId));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (MmsMessageTemplate t : list) {
            stringRedisTemplate.opsForZSet().add(getKey(userId), JSON.toJSONString(t), t.getPriority().doubleValue());
        }

        // 订阅发布模式订阅
        stringRedisTemplate.convertAndSend(MmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC, "all");
    }

    @Override
    public boolean reloadToRedis() {
        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("彩信模板数据为空");
            return false;
        }

        stringRedisTemplate.delete(stringRedisTemplate.keys(MmsRedisConstant.RED_USER_MESSAGE_TEMPLATE + "*"));

        for (MmsMessageTemplate template : list) {
            try {
                stringRedisTemplate.opsForZSet().add(getKey(template.getUserId()), JSON.toJSONString(template),
                                                     template.getPriority().doubleValue());

            } catch (Exception e) {
                logger.warn("REDIS 彩信模板设置失败", e);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean delete(long id, int userId) {
        MmsMessageTemplate originTemplate = null;
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

        return mmsMessageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public MmsMessageTemplate getWithUserId(Long id, int userId) {
        return null;
    }

    @Override
    public MmsMessageTemplate getByModelId(String modelId) {
        return mmsMessageTemplateMapper.selectByModelId(modelId);
    }

    @Override
    public boolean isModelIdAvaiable(String modelId, int userId) {
        MmsMessageTemplate template = getByModelId(modelId);
        if (template == null) {
            logger.error("模板ID[" + modelId + "]数据为空");
            return false;
        }

        if (userId == 0 || template.getUserId() != userId) {
            logger.error("模板ID[" + modelId + "] 用户[" + userId + "]数据权限不符");
            return false;
        }

        if (ApproveStatus.SUCCESS.getValue() != template.getStatus()) {
            logger.error("模板ID[" + modelId + "] 状态[" + template.getStatus() + "]下不可用");
            return false;
        }

        return true;
    }

    @Override
    public String checkBodyRuleIsRight(String body) throws BodyCheckException {
        // body:[{mediaName:”test.jpg”,mediaType:”image”,content:”=bS39888993#jajierj*...”}]
        if (StringUtils.isEmpty(body)) {
            throw new BodyCheckException("Body is empty.");
        }

        try {
            List<Map<String, String>> list = JSON.parseObject(body, new TypeReference<List<Map<String, String>>>() {
            });

            List<MmsMessageTemplateBody> bodyReport = new ArrayList<>();
            int sort = 0;
            for (Map<String, String> map : list) {
                String mediaType = map.get(NODE_MEDIA_TYPE);
                if (StringUtils.isEmpty(mediaType)) {
                    throw new BodyCheckException("Body's node[mediaType] is empty");
                }

                if (!MediaType.isTypeRight(mediaType)) {
                    throw new BodyCheckException("Body's node[mediaType:'" + mediaType + "'] is not avaiable.");
                }

                String mediaName = map.get(NODE_MEDIA_NAME);
                if (StringUtils.isEmpty(mediaName)) {
                    throw new BodyCheckException("Body's node[mediaName] is empty.");
                }

                String content = map.get(NODE_CONTENT);
                if (StringUtils.isEmpty(content)) {
                    throw new BodyCheckException("Body's node[content] is empty.");
                }

                boolean isUtf8 = !isByteType(mediaType);

                byte[] data = parse2Byte(content, isUtf8);
                if (data == null) {
                    throw new BodyCheckException("Body's node[content:'" + content + "'] translate failed.");
                }

                sort++;
                MmsMessageTemplateBody templateBody = new MmsMessageTemplateBody();
                templateBody.setMediaName(mediaName);
                templateBody.setMediaType(mediaType.toLowerCase());
                templateBody.setData(data);
                templateBody.setContent(mmsMediaFileService.generateFileName(MediaFileDirectory.CUSTOM_BODY_FILE_DIR,
                                                                             templateBody.getMediaType()));
                templateBody.setSort(sort);

                bodyReport.add(templateBody);
            }

            String finalContext = null;
            if (CollectionUtils.isNotEmpty(bodyReport)) {
                for (MmsMessageTemplateBody templateBody : bodyReport) {
                    mmsMediaFileService.writeFileWithFileName(templateBody.getContent(), templateBody.getData());
                }

                finalContext = mmsMediaFileService.writeFile(MediaFileDirectory.CUSTOM_BODY_DIR,
                                                             JSON.toJSONString(bodyReport));
            }

            return finalContext;
        } catch (Exception e) {
            logger.error("自定义内容[" + body + "]解析错误", e);
            throw new BodyCheckException("Body translate failed");
        }
    }

    @Override
    public String bodyToContext(String body) {
        return null;
    }

    /**
     * 是否属于BYTE模式
     * 
     * @param mediaType
     * @return
     */
    private static boolean isByteType(String mediaType) {
        if (StringUtils.isEmpty(mediaType)) {
            return false;
        }

        return MediaType.IMAGE.getCode().equals(mediaType) || MediaType.AUDIO.getCode().equals(mediaType)
               || MediaType.VIDEO.getCode().equals(mediaType);
    }

    /**
     * 转换成为BYTE数据
     * 
     * @param content
     * @return
     */
    private byte[] parse2Byte(String content, boolean isUtf8) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        try {
            if (isUtf8) {
                return content.getBytes(Charset.forName(MmsMediaFileService.ENCODING));
            }

            return Base64.decodeBase64(content);
        } catch (Exception e) {
            logger.error("内容[" + content + "]转换Base64失败", e);
            return null;
        }
    }

}
