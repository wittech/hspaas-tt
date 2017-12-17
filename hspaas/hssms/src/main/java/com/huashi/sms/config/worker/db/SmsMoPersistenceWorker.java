package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.service.ISmsMoMessageService;

/**
 * 
  * TODO 短信上行持久线程
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月23日 上午10:14:30
 */
public class SmsMoPersistenceWorker extends AbstractWorker<SmsMoMessageReceive> {

	public SmsMoPersistenceWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	protected void operate(List<SmsMoMessageReceive> list) {
		try {
			if (CollectionUtils.isEmpty(list)) {
                return;
            }

			long startTime = System.currentTimeMillis();
			getInstance(ISmsMoMessageService.class).batchInsert(list);
			logger.info("短信上行信息持久同步完成，共处理  {} 条, 耗时： {} ms", list.size(), System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			logger.error("短信上行信息异步持久化失败", e);
			super.backupIfFailed(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_FAILED_LIST, list);
		} finally {
			super.clear(list);
		}
		
	}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_LIST;
	}
	
}
