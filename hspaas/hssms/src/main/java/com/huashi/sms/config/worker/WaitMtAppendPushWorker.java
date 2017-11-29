package com.huashi.sms.config.worker;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
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

	public WaitMtAppendPushWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void operate(List list) {
		try {
			getInstance(ISmsMtPushService.class).sendToWaitPushQueue(list);
		} catch (Exception e) {
			logger.error("补偿推送失败", e);
		} finally {
			timer.set(0);
			list.clear();
		}
	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_READY_APPEND_PUSH;
	}

	@Override
	protected int scanSize() {
		return 1000;
	}

	@Override
	protected long timeout() {
		return super.timeout();
	}
	
}
