package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.service.ISmsMtDeliverService;

/**
 * 
 * TODO 短信主任务待持久线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class SmsDeliverPersistenceWorker extends AbstractWorker {
	
	public SmsDeliverPersistenceWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void operate(List list) {
		try {
			if (CollectionUtils.isEmpty(list))
				return;

			long startTime = System.currentTimeMillis();
			getInstance(ISmsMtDeliverService.class).batchInsert(list);
			logger.info("短信状态回执信息持久同步完成，共处理  {} 条, 耗时： {} ms", list.size(), System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			logger.error("短信状态回执异步持久化失败", e);
			super.backupIfFailed(SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_FAILED_LIST, list);
		} finally {
			super.clear(list);
		}
	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_LIST;
	}

}
