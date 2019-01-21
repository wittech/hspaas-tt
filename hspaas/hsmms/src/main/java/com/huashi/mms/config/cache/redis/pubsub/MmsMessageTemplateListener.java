package com.huashi.mms.config.cache.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.huashi.mms.template.service.MmsTemplateService;

/**
 * TODO 短信模板广播监听
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年9月3日 上午12:14:52
 */
public class MmsMessageTemplateListener extends MessageListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            if (message == null) {
                return;
            }

            MmsTemplateService.GLOBAL_MESSAGE_TEMPLATE.clear();

        } catch (Exception e) {
            logger.warn("黑名单订阅数据失败", e);
        }
    }
}
