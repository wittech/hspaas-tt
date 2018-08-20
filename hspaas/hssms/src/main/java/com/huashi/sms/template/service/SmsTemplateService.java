package com.huashi.sms.template.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
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
    private Logger                                   logger                  = LoggerFactory.getLogger(getClass());

    /**
     * 全局短信模板（与REDIS 同步采用广播模式）
     */
    public volatile static Map<Integer, Set<String>> GLOBAL_MESSAGE_TEMPLATE = new HashMap<>();

    @Override
    public PaginationVo<MessageTemplate> findPage(int userId, String modeIds, String title, Integer type,
                                                  Integer status, Integer pageNo, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (StringUtils.isNotEmpty(modeIds)) {
            params.put("ids", modeIds.split(","));
        }
        if (status != null) {
            params.put("status", transferStatus(status));
        }
        if (StringUtils.isNotEmpty(title)) {
            params.put("remark", title);
        }
        
        if (type != null) {
            params.put("notice_mode", type);
        }
        
        int totalRecord = messageTemplateMapper.getCountByUserId(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("startPage", PaginationVo.getStartPage(pageNo == null ? PaginationVo.DEFAULT_START_PAGE_NO : pageNo));
        params.put("pageRecord", pageSize == null ? PaginationVo.DEFAULT_RECORD_PER_PAGE : pageSize);

        List<MessageTemplate> list = messageTemplateMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, pageNo, totalRecord);
    }

    /**
     * TODO 转义大数据定义的状态 大数据： 1:审核中，2:审核通过，3:审核失败
     * 
     * @param status
     * @return
     */
    private static int transferStatus(Integer status) {
        if (status == null || status == 1) {
            return ApproveStatus.WAITING.getValue();
        } else if (status == 2) {
            return ApproveStatus.SUCCESS.getValue();
        } else {
            return ApproveStatus.FAIL.getValue();
        }
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
        MessageTemplate td = get(template.getId());
        if (td == null) {
            logger.error("短信模板信息为空, id:{}", template.getId());
            return false;
        }

//        template.setRegexValue(parseContent2Regex(template.getContent()));
        template.setCreateTime(td.getCreateTime());
        int result = messageTemplateMapper.updateByPrimaryKeySelective(template);
        if (result > 0) {
            reloadUserTemplate(template.getUserId());
        }
        return result > 0;
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

    @Override
    public long save(MessageTemplate template) {
        if (StringUtils.isEmpty(template.getContent())) {
            throw new IllegalArgumentException("模板内容不能为空");
        }

        if (forbiddenWordsService.isContainsForbiddenWords(template.getContent())) {
            Set<String> words = forbiddenWordsService.filterForbiddenWords(template.getContent());
            throw new IllegalArgumentException(String.format("模板内容包含敏感词：%s", words));
        }
        template.setCreateTime(new Date());

        template.setStatus(ApproveStatus.WAITING.getValue());
        // 融合平台判断 后台添加 状态默认
        // if (AppType.WEB.getCode() == template.getAppType()) {
        // template.setStatus(ApproveStatus.WAITING.getValue());
        // }

        // 暂时用做 存储签名使用
//        template.setRegexValue(parseContent2Regex(template.getContent()));

        if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            pushToRedis(template.getUserId(), template);
        }

        int result = messageTemplateMapper.insertSelective(template);
        if (result > 0) {
            return template.getId();
        }

        return 0;
    }

    @Override
    @Transactional
    public boolean saveToBatchContent(MessageTemplate template, String[] contents) {
        if (forbiddenWordsService.isContainsForbiddenWords(template.getContent())) {
            Set<String> words = forbiddenWordsService.filterForbiddenWords(template.getContent());
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
//            template.setRegexValue(parseContent2Regex(content));

            long result = messageTemplateMapper.insertSelective(template);
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
        
        return true;
//        MessageTemplate template = get(id);
//        return template != null && PatternUtil.isRight(template.getRegexValue(), content);
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
            pushToRedis(template.getUserId(), template);
        }

        return true;
    }

}
