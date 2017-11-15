package com.huashi.sms.config.worker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.record.domain.SmsMoMessagePush;
import com.huashi.sms.record.service.ISmsMoPushService;

/**
 * 
 * TODO 短信上行推送信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class SmsWaitMoPushPersistenceWorker implements Runnable {

	private StringRedisTemplate stringRedisTemplate;
	private ISmsMoPushService smsMoPushService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SmsWaitMoPushPersistenceWorker(StringRedisTemplate stringRedisTemplate, ISmsMoPushService smsMoPushService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsMoPushService = smsMoPushService;
	}

	// 批量扫描大小
	private static final int SCAN_BATCH_SIZE = 1000;

	@Override
	public void run() {
		while (true) {
			try {
				stringRedisTemplate.watch(SmsRedisConstant.RED_DB_MESSAGE_MO_PUSH_LIST);
				// SCAN_BATCH_SIZE - 1， 是因为REDIS LRANGE 默认从0索引算起，0-999 则为1000个
				List<String> list = stringRedisTemplate.opsForList().range(SmsRedisConstant.RED_DB_MESSAGE_MO_PUSH_LIST, 0,
						SCAN_BATCH_SIZE - 1);
				
				if(CollectionUtils.isEmpty(list))
					continue;
				
				List<SmsMoMessagePush> pushes = new ArrayList<>();
				for (String t : list) {
					pushes.add(JSON.parseObject(t, new TypeReference<SmsMoMessagePush>(){}));
				}

				doPersistence(list, pushes);
				
				// 保留自本次的SCAN_BATCH_SIZE至最后数据，清除本次处理的1000条
				stringRedisTemplate.opsForList().trim(SmsRedisConstant.RED_DB_MESSAGE_MO_PUSH_LIST, list.size(), -1);
				stringRedisTemplate.unwatch();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * TODO 持久化主任务
	 * 
	 * @param list
	 * @param tasks
	 * @param taskPackets
	 */
	private void doPersistence(List<String> list, List<SmsMoMessagePush> pushes) {
		try {
			smsMoPushService.savePushMessage(pushes);
			logger.info("短信上行推送信息持久同步完成，共处理  {} 条", pushes.size());
		} catch (Exception e) {
			logger.error("短信上行推送信息异步持久化失败", e);
			// 处理失败放置处理失败队列中
			stringRedisTemplate.opsForList().rightPushAll(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_FAILED_LIST, list);
		}
	}

}
