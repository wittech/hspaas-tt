package com.huashi.sms.config.worker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtPushService;

/**
 * 
 * TODO 短信主任务待持久线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class WaitMtAppendPushWorker extends BaseWorker implements Runnable {

	private ISmsMtPushService smsMtPushService;

	public WaitMtAppendPushWorker(StringRedisTemplate stringRedisTemplate, ISmsMtPushService smsMtPushService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsMtPushService = smsMtPushService;
	}

	// 批量扫描大小
	private static final int SCAN_BATCH_SIZE = 1000;

	@Override
	public void run() {
		List<SmsMtMessageDeliver> delivers = new ArrayList<SmsMtMessageDeliver>();
		while (true) {
			try {
				if(timer.get() == 0)
					timer.set(System.currentTimeMillis());
				
				// 如果本次量达到批量取值数据，则跳出
				if(delivers.size() >= SCAN_BATCH_SIZE) {
					logger.info("-----------获取补偿size:{}", delivers.size());
					send(delivers);
					continue;
				}
				
				// 如果本次循环时间超过5秒则跳出
				if(System.currentTimeMillis() - timer.get() >= 1000) {
					send(delivers);
					continue;
				}
				
				Object o = stringRedisTemplate.opsForList().leftPop(SmsRedisConstant.RED_READY_APPEND_PUSH);
				// 执行到redis中没有数据为止
				if (o == null) {
					if(CollectionUtils.isNotEmpty(delivers)) {
						logger.info("-----------取完补偿，获取size:{}, 耗时：{}ms", delivers.size(), System.currentTimeMillis() - timer.get());
						send(delivers);
					}

					continue;
				}
				
				delivers.addAll(JSON.parseObject(o.toString(), new TypeReference<List<SmsMtMessageDeliver>>(){}));
				
			} catch (Exception e) {
				logger.error("推送补偿对账异常", e);
			} finally {
				timer.set(0);
				delivers.clear();
			}
		}
	}
	
	public void send(List<SmsMtMessageDeliver> delivers) {
		try {
			smsMtPushService.sendToWaitPushQueue(delivers);
		} catch (Exception e) {
			logger.error("补偿推送失败", e);
		} finally {
			timer.set(0);
			delivers.clear();
		}
	}

}
