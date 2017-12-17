package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMoMessagePush;
import com.huashi.sms.record.service.ISmsMoPushService;

/**
 * 
 * TODO 短信上行推送信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class SmsMoPushPersistenceWorker extends AbstractWorker<SmsMoMessagePush> {

	public SmsMoPushPersistenceWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	protected void operate(List<SmsMoMessagePush> list) {
		try {
			if (CollectionUtils.isEmpty(list)) {
                return;
            }

			long startTime = System.currentTimeMillis();
			getInstance(ISmsMoPushService.class).savePushMessage(list);
			logger.info("短信上行推送持久同步完成，共处理  {} 条, 耗时： {} ms", list.size(), System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			logger.error("短信上行推送异步持久化失败", e);
			super.backupIfFailed(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_FAILED_LIST, list);
		} finally {
			super.clear(list);
		}
	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_DB_MESSAGE_MO_PUSH_LIST;
	}
	
}
