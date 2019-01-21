package com.huashi.mms.config.worker.fork;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.huashi.mms.config.worker.AbstractWorker;
import com.huashi.mms.record.service.IMmsMtPushService;

/**
 * 
  * TODO 针对短信下行报告推送给下家（首次数据未入库或者REDIS无相关数据，后续追加推送）
  * @author zhengying
  * @version V1.0   
  * @date 2017年12月5日 下午5:46:12
 */
public class MtReportPushToDeveloperWorker extends AbstractWorker<JSONObject> {

	private String developerPushQueueName;
	
	public MtReportPushToDeveloperWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	public MtReportPushToDeveloperWorker(ApplicationContext applicationContext, String developerPushQueueName) {
		super(applicationContext);
		this.developerPushQueueName = developerPushQueueName;
	}
	
	@Override
	protected void operate(List<JSONObject> list) {
		if(CollectionUtils.isEmpty(list)) {
            return;
        }

		getInstance(IMmsMtPushService.class).pushMessageBodyToDeveloper(list);
	}
	

	@Override
	protected String redisKey() {
		return developerPushQueueName;
	}

	@Override
	protected String jobTitle() {
		return "彩信状态报告推送";
	}

	@Override
	protected long timeout() {
		return super.timeout();
	}
	
}
