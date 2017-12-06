package com.huashi.sms.config.worker.fork;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.service.ISmsMtPushService;

/**
 * 
  * TODO 针对短信下行报告推送给下家（首次数据未入库或者REDIS无相关数据，后续追加推送）
  * @author zhengying
  * @version V1.0   
  * @date 2017年12月5日 下午5:46:12
 */
public class MtReportPushToDeveloperWorker extends AbstractWorker implements Runnable {

	private String developerPushQueueName;
	
	public MtReportPushToDeveloperWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	public MtReportPushToDeveloperWorker(ApplicationContext applicationContext, String developerPushQueueName) {
		super(applicationContext);
		this.developerPushQueueName = developerPushQueueName;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void operate(List list) {
		if(CollectionUtils.isEmpty(list))
			return;
		
		try {
			getInstance(ISmsMtPushService.class).pushMessageBodyToDeveloper(list);
		} catch (Exception e) {
			logger.error("推送下行状态报告失败", e);
		} finally {
			timer.set(0);
			list.clear();
		}
	}
	

	@Override
	protected String redisKey() {
		return developerPushQueueName;
	}

	@Override
	protected long timeout() {
		return super.timeout();
	}
	
}
