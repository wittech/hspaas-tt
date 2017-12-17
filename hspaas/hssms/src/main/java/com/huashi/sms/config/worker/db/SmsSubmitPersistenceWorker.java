package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
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
public class SmsSubmitPersistenceWorker extends AbstractWorker<SmsMtMessageSubmit> {

	public SmsSubmitPersistenceWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	protected void operate(List<SmsMtMessageSubmit> list) {
		ISmsMtSubmitService smsSubmitService = getInstance(ISmsMtSubmitService.class);
		try {
			if (CollectionUtils.isEmpty(list)) {
                return;
            }

			long startTime = System.currentTimeMillis();
			smsSubmitService.batchInsertSubmit(list);
			logger.info("短信提交信息持久同步完成，共处理  {} 条, 耗时： {} ms", list.size(), System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			logger.error("短信提交信息异步持久化失败", e);
			super.backupIfFailed(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_FAILED_LIST, list);
		} finally {
			super.clear(list);
		}

	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST;
	}


}
