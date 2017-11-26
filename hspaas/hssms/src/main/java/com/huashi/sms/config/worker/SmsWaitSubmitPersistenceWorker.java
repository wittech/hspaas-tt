package com.huashi.sms.config.worker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
public class SmsWaitSubmitPersistenceWorker extends BaseWorker implements Runnable {

	private ISmsMtSubmitService smsSubmitService;

	public SmsWaitSubmitPersistenceWorker(
			StringRedisTemplate stringRedisTemplate,
			ISmsMtSubmitService smsSubmitService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsSubmitService = smsSubmitService;
	}

	@Override
	public void run() {
		List<SmsMtMessageSubmit> submits = new ArrayList<SmsMtMessageSubmit>();
		try {
			while (true) {
				if(timer.get() == 0)
					timer.set(System.currentTimeMillis());
				
				// 如果本次量达到批量取值数据，则跳出
				if(submits.size() >= SCAN_BATCH_SIZE) {
					logger.info("-----------获取size:{}", submits.size());
					doPersistence(submits);
					continue;
				}
				
				// 如果本次循环时间超过5秒则跳出
				if(System.currentTimeMillis() - timer.get() >= 5000) {
					doPersistence(submits);
					continue;
				}
				
				Object o = stringRedisTemplate.opsForList().leftPop(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST);
				// 执行到redis中没有数据为止
				if (o == null) {
					if(CollectionUtils.isNotEmpty(submits)) {
						logger.info("-----------取完，获取size:{}, 耗时：{}ms", submits.size(), System.currentTimeMillis() - timer.get());
						doPersistence(submits);
					}

					continue;
				}
				
				submits.addAll(JSON.parseObject(o.toString(), new TypeReference<List<SmsMtMessageSubmit>>(){}));
			}

		} catch (Exception e) {
			logger.error("短信提交数据入库失败，数据为：{}", JSON.toJSONString(submits), e);
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
	private void doPersistence(List<SmsMtMessageSubmit> submits) {
		try {
			if(CollectionUtils.isEmpty(submits))
				return;
			
			long startTime = System.currentTimeMillis();
			smsSubmitService.batchInsertSubmit(submits);
			logger.info("短信提交信息持久同步完成，共处理  {} 条, 耗时： {} ms", submits.size(), System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			logger.error("短信提交信息异步持久化失败", e);
			// 处理失败放置处理失败队列中
			stringRedisTemplate.opsForList().rightPushAll(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_FAILED_LIST, JSON.toJSONString(submits));
		} finally {
			timer.set(0);
			submits.clear();
		}
	}

}
