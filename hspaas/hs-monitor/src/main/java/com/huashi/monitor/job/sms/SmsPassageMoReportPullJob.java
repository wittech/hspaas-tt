package com.huashi.monitor.job.sms;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.huashi.common.util.DateUtil;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.monitor.job.AbstractJob;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.service.ISmsMoMessageService;

public class SmsPassageMoReportPullJob extends AbstractJob {

    private ISmsMoMessageService   smsMoMessageService;
    private ISmsProviderService    smsProviderService;
    private IPassageMonitorService passageMonitorService;

    public SmsPassageMoReportPullJob(ISmsMoMessageService smsMoMessageService, ISmsProviderService smsProviderService,
                                     IPassageMonitorService passageMonitorService) {
        super();
        this.smsMoMessageService = smsMoMessageService;
        this.smsProviderService = smsProviderService;
        this.passageMonitorService = passageMonitorService;
    }

    @Override
    public void run(ShardingContext context) {
        if (StringUtils.isEmpty(context.getJobParameter())) {
            logger.warn("无法识别通道信息");
            return;
        }

        try {
            SmsPassageAccess smsPassageAccess = JSON.parseObject(context.getJobParameter(), SmsPassageAccess.class);

            long start = System.currentTimeMillis();
            List<SmsMoMessageReceive> list = smsProviderService.pullMoReport(smsPassageAccess);
            long costTime = System.currentTimeMillis() - start;
            if (CollectionUtils.isNotEmpty(list)) {

                int avaiableCount = smsMoMessageService.doFinishReceive(list);
                logger.info("短信通道轮训上行回执信息共获取{}条，共处理有效数据{}条", list.size(), avaiableCount);

                PassagePullReport report = new PassagePullReport();
                report.setLastTime(DateUtil.getNow());
                report.setLastAmount(list.size());
                report.setCostTime(costTime);
                report.setPullAvaiableTimes(avaiableCount);

                passageMonitorService.updatePullReportToRedis(getJobDes(context), report);
            }

        } catch (Exception e) {
            logger.error("短信通道获取上行处理失败", e);
        }
    }
}
