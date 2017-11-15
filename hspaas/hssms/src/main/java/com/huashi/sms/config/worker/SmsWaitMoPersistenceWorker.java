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
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.service.ISmsMoMessageService;

/**
 * 
  * TODO 短信上行持久线程
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月23日 上午10:14:30
 */
public class SmsWaitMoPersistenceWorker implements Runnable {

	private StringRedisTemplate stringRedisTemplate;
	private ISmsMoMessageService smsMoMessageService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SmsWaitMoPersistenceWorker(StringRedisTemplate stringRedisTemplate, ISmsMoMessageService smsMoMessageService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsMoMessageService = smsMoMessageService;
	}

	// 批量扫描大小
	private static final int SCAN_BATCH_SIZE = 1000;

	@Override
	public void run() {
		while (true) {
			try {
				stringRedisTemplate.watch(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_LIST);
				// SCAN_BATCH_SIZE - 1， 是因为REDIS LRANGE 默认从0索引算起，0-999 则为1000个
				
//				stringRedisTemplate.opsForList().leftPop(key, timeout, unit)
				
				List<String> list = stringRedisTemplate.opsForList().range(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_LIST, 0,
						SCAN_BATCH_SIZE - 1);
				
				if(CollectionUtils.isEmpty(list))
					continue;
				
				List<SmsMoMessageReceive> receives = new ArrayList<>();
				for (String t : list) {
					receives.addAll(JSON.parseObject(t, new TypeReference<List<SmsMoMessageReceive>>(){}));
				}

				doPersistence(list, receives);
				
				// 保留自本次的SCAN_BATCH_SIZE至最后数据，清除本次处理的1000条
				stringRedisTemplate.opsForList().trim(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_LIST, list.size(), -1);
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
	private void doPersistence(List<String> list, List<SmsMoMessageReceive> receives) {
		try {
			smsMoMessageService.batchInsert(receives);
			logger.info("短信上行信息持久同步完成，共处理  {} 条", receives.size());
		} catch (Exception e) {
			logger.error("短信上行信息异步持久化失败", e);
			// 处理失败放置处理失败队列中
			stringRedisTemplate.opsForList().rightPushAll(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_FAILED_LIST, list);
		}
	}

}
