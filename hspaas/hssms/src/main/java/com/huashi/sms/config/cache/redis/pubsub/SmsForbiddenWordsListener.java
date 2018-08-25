package com.huashi.sms.config.cache.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 
  * TODO 敏感词监控
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2018年8月25日 下午4:34:47
 */
public class SmsForbiddenWordsListener extends MessageListenerAdapter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			if(message == null) {
                return;
            }
			
		} catch (Exception e) {
			logger.warn("敏感词订阅数据失败", e);
		}
	}
}
