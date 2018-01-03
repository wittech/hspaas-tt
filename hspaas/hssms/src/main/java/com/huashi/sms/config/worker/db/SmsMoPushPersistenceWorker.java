package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMoMessagePush;
import com.huashi.sms.record.service.ISmsMoPushService;

/**
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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        getInstance(ISmsMoPushService.class).savePushMessage(list);
    }

    @Override
    protected String redisKey() {
        return SmsRedisConstant.RED_DB_MESSAGE_MO_PUSH_LIST;
    }

    @Override
    protected String jobTitle() {
        return "短信上行推送持久化";
    }

}
