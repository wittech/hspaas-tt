package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMtMessagePush;
import com.huashi.sms.record.service.ISmsMtPushService;

/**
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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        getInstance(ISmsMtPushService.class).savePushMessage(list);
    }

    @Override
    protected String redisKey() {
        return SmsRedisConstant.RED_DB_MESSAGE_MT_PUSH_LIST;
    }

    @Override
    protected String jobTitle() {
        return "短信下行推送持久化";
    }

}
