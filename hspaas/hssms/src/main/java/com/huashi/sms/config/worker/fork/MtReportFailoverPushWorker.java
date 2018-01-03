package com.huashi.sms.config.worker.fork;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtPushService;

/**
 * TODO 针对短信下行报告推送（首次数据未入库或者REDIS无相关数据，后续追加推送）
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年12月5日 下午5:45:18
 */
public class MtReportFailoverPushWorker extends AbstractWorker<SmsMtMessageDeliver> {

    @Override
    protected String jobTitle() {
        return "短信状态补偿轮训";
    }

    public MtReportFailoverPushWorker(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void operate(List<SmsMtMessageDeliver> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        getInstance(ISmsMtPushService.class).compareAndPushBody(list);
    }

    @Override
    protected String redisKey() {
        return SmsRedisConstant.RED_QUEUE_SMS_DELIVER_FAILOVER;
    }

    @Override
    protected int scanSize() {
        return 1000;
    }

    @Override
    protected long timeout() {
        return super.timeout();
    }

}
