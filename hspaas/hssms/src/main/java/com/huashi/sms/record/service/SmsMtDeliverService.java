package com.huashi.sms.record.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.druid.util.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.record.dao.SmsMtMessageDeliverMapper;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * 短信回执服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月14日 上午11:03:28
 */
@Service
public class SmsMtDeliverService implements ISmsMtDeliverService {

    @Autowired
    private ISmsMtPushService         smsMtPushService;
    @Resource
    private StringRedisTemplate       stringRedisTemplate;
    @Autowired
    private SmsMtMessageDeliverMapper smsMtMessageDeliverMapper;

    private final Logger              logger = LoggerFactory.getLogger(getClass());

    @Override
    public SmsMtMessageDeliver findByMobileAndMsgid(String mobile, String msgId) {
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(msgId)) {
            return null;
        }

        return smsMtMessageDeliverMapper.selectByMobileAndMsgid(msgId, mobile);
    }

    @Override
    public void batchInsert(List<SmsMtMessageDeliver> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        smsMtMessageDeliverMapper.batchInsert(list);
    }

    @Override
    public int doFinishDeliver(List<SmsMtMessageDeliver> delivers) {
        if (CollectionUtils.isEmpty(delivers)) {
            return 0;
        }

        try {

            // 将待推送消息发送至用户队列进行处理（2017-03-20 合包处理），异步执行
            smsMtPushService.compareAndPushBody(delivers);

            batchInsert(delivers);

            logger.info("Deliver messages[" + JSON.toJSONString(delivers) + "] has enqueued");

            return delivers.size();
        } catch (Exception e) {
            logger.error("处理待回执信息REDIS失败，失败信息：{}", JSON.toJSONString(delivers), e);
            throw new RuntimeException("状态报告回执处理失败");
        }
    }

    @Override
    public boolean doDeliverToException(JSONObject obj) {
        try {
            return stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_MESSAGE_STATUS_RECEIPT_EXCEPTION_LIST,
                                                              JSON.toJSONString(obj)) > 0;
        } catch (Exception e) {
            logger.error("发送回执错误信息失败 {}", JSON.toJSON(obj), e);
            return false;
        }
    }

}
