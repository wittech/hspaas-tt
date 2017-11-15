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
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;

/**
 * 
  * TODO 待提交短信持久化线程
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月14日 上午10:26:33
 */
public class SmsWaitSubmitPersistenceWorker implements Runnable {

	private StringRedisTemplate stringRedisTemplate;
	private ISmsMtSubmitService smsSubmitService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SmsWaitSubmitPersistenceWorker(StringRedisTemplate stringRedisTemplate, ISmsMtSubmitService smsSubmitService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsSubmitService = smsSubmitService;
	}

	// 批量扫描大小
	private static final int SCAN_BATCH_SIZE = 1000;

	@Override
	public void run() {
		while (true) {
			List<String> list = null;
			try {
				stringRedisTemplate.watch(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST);
				// SCAN_BATCH_SIZE - 1， 是因为REDIS LRANGE 默认从0索引算起，0-999 则为1000个
				list = stringRedisTemplate.opsForList().range(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST, 0,
						SCAN_BATCH_SIZE - 1);
				
				if(CollectionUtils.isEmpty(list))
					continue;
				
				List<SmsMtMessageSubmit> submits = new ArrayList<>();
				for (String t : list) {
					submits.addAll(JSON.parseObject(t, new TypeReference<List<SmsMtMessageSubmit>>(){}));
				}

				doPersistence(list, submits);
				
				// 保留自本次的SCAN_BATCH_SIZE至最后数据，清除本次处理的1000条
				stringRedisTemplate.opsForList().trim(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST, list.size(), -1);
				stringRedisTemplate.unwatch();

			} catch (Exception e) {
				logger.error("短信提交数据入库失败，数据为：{}", JSON.toJSONString(list), e);
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
	private void doPersistence(List<String> list, List<SmsMtMessageSubmit> submits) {
		try {
			smsSubmitService.batchInsertSubmit(submits);
			logger.info("短信提交信息持久同步完成，共处理  {} 条", submits.size());
		} catch (Exception e) {
			logger.error("短信提交信息异步持久化失败", e);
			// 处理失败放置处理失败队列中
			stringRedisTemplate.opsForList().rightPushAll(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_FAILED_LIST, list);
		}
	}

}
