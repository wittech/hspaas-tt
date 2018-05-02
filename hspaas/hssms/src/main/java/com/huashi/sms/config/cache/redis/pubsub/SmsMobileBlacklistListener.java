package com.huashi.sms.config.cache.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant.MessageAction;
import com.huashi.sms.settings.service.SmsMobileBlackListService;

/**
 * 
  * TODO 黑名单手机广播监听
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年9月3日 上午12:14:25
 */
@Component
public class SmsMobileBlacklistListener extends MessageListenerAdapter {

	public SmsMobileBlacklistListener(StringRedisTemplate stringRedisTemplate) {
		super();
		this.stringRedisTemplate = stringRedisTemplate;
	}

	private StringRedisTemplate stringRedisTemplate;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			if(message == null) {
                return;
            }
			
			String[] report = message.toString().split(":");
			if(report==null || report.length==0){
				logger.warn("黑名单订阅数据失败");
				return;
			}
			
			MessageAction action = MessageAction.parse(Integer.parseInt(report[0]));
			if(MessageAction.ADD == action) {
				SmsMobileBlackListService.GLOBAL_MOBILE_BLACKLIST.put(report[1], Integer.parseInt(report[2]));
				stringRedisTemplate.opsForHash().put(SmsRedisConstant.RED_MOBILE_BLACKLIST, report[1], report[2]);
			} else if(MessageAction.REMOVE == action) {
				SmsMobileBlackListService.GLOBAL_MOBILE_BLACKLIST.remove(report[1]);
				stringRedisTemplate.opsForHash().delete(SmsRedisConstant.RED_MOBILE_BLACKLIST, report[1]);
			}
			
		} catch (Exception e) {
			logger.warn("黑名单订阅数据失败", e);
		}
	}
}
