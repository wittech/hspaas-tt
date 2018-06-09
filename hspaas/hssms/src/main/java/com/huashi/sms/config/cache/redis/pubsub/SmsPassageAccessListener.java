package com.huashi.sms.config.cache.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.huashi.sms.passage.service.SmsPassageAccessService;

/**
 * 
  * TODO 可用通道广播监听
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2018年6月9日 下午3:04:21
 */
public class SmsPassageAccessListener extends MessageListenerAdapter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			if(message == null) {
                return;
            }
			
			// 清空后，采用延期加载方式填充数据，即使用的时候才会去REDIS查询并填充
			SmsPassageAccessService.GLOBAL_PASSAGE_ACCESS_CONTAINER.clear();
			
		} catch (Exception e) {
			logger.warn("黑名单订阅数据失败", e);
		}
	}
}
