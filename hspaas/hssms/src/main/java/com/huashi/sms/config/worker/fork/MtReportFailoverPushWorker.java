package com.huashi.sms.config.worker.fork;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.service.ISmsMtPushService;

/**
 * 
  * TODO 针对短信下行报告推送（首次数据未入库或者REDIS无相关数据，后续追加推送）
  * @author zhengying
  * @version V1.0   
  * @date 2017年12月5日 下午5:45:18
 */
public class MtReportFailoverPushWorker extends AbstractWorker implements Runnable {

	public MtReportFailoverPushWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void operate(List list) {
		if(CollectionUtils.isEmpty(list))
			return;
		
		try {
			getInstance(ISmsMtPushService.class).compareAndPushBody(list);
		} catch (Exception e) {
			logger.error("补偿推送失败", e);
		} finally {
			timer.set(0);
			list.clear();
		}
	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_QUEUE_SMS_DELIVER_FAILOVER;
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
