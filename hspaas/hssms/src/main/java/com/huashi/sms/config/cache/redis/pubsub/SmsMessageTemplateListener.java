package com.huashi.sms.config.cache.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import com.huashi.sms.template.service.SmsTemplateService;

/**
 * 
  * TODO 短信模板广播监听
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年9月3日 上午12:14:52
 */
@Component
public class SmsMessageTemplateListener extends MessageListenerAdapter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			if(message == null) {
                return;
            }
			
			SmsTemplateService.GLOBAL_MESSAGE_TEMPLATE.clear();
			
		} catch (Exception e) {
			logger.warn("黑名单订阅数据失败", e);
		}
	}
}
