package com.huashi.mms.config.worker.fork;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.mms.config.cache.redis.constant.MmsRedisConstant;
import com.huashi.mms.config.worker.AbstractWorker;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.record.service.IMmsMtPushService;

/**
 * TODO 针对短信下行报告推送（首次数据未入库或者REDIS无相关数据，后续追加推送）
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年12月5日 下午5:45:18
 */
public class MtReportFailoverPushWorker extends AbstractWorker<MmsMtMessageDeliver> {

    @Override
    protected String jobTitle() {
        return "彩信状态补偿轮训";
    }

    public MtReportFailoverPushWorker(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void operate(List<MmsMtMessageDeliver> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        getInstance(IMmsMtPushService.class).compareAndPushBody(list);
    }

    @Override
    protected String redisKey() {
        return MmsRedisConstant.RED_QUEUE_MMS_DELIVER_FAILOVER;
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
