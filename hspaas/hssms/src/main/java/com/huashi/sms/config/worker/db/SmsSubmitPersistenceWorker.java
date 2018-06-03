package com.huashi.sms.config.worker.db;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.huashi.sms.config.worker.AbstractWorker;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;

/**
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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        smsSubmitService.batchInsertSubmit(list);
    }

    @Override
    protected String redisKey() {
//        return SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST;
        return null;
    }

    @Override
    protected String jobTitle() {
        return "短信提交持久化";
    }

}
