package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.service.ISmsMoMessageService;

/**
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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        getInstance(ISmsMoMessageService.class).batchInsert(list);
    }

    @Override
    protected String redisKey() {
        return SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_LIST;
    }

    @Override
    protected String jobTitle() {
        return "短信上行短信持久化";
    }
}
