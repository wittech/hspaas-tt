package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMtMessagePush;
import com.huashi.sms.record.service.ISmsMtPushService;

/**
 * 
 * TODO 短信主任务待持久线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class SmsMtPushPersistenceWorker extends AbstractWorker<SmsMtMessagePush> {

	public SmsMtPushPersistenceWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	protected void operate(List<SmsMtMessagePush> list) {
		try {
			if (CollectionUtils.isEmpty(list)) {
                return;
            }

			long startTime = System.currentTimeMillis();
			getInstance(ISmsMtPushService.class).savePushMessage(list);
			logger.info("短信推送信息持久同步完成，共处理  {} 条, 耗时： {} ms", list.size(), System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			logger.error("短信推送信息异步持久化失败", e);
			super.backupIfFailed(SmsRedisConstant.RED_DB_MESSAGE_MT_PUSH_FAILED_LIST, list);
		} finally {
			super.clear(list);
		}
	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_DB_MESSAGE_MT_PUSH_LIST;
	}
	
}
