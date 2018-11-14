package com.huashi.monitor.job;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.huashi.sms.report.service.ISmsSubmitHourReportService;

/**
 * TODO 每小时离线生成短信提交报告数据
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年11月13日 下午5:57:20
 */
@Service("smsSubmitReportBornJob")
public class SmsSubmitReportBornJob extends AbtractJob {

    @Reference
    private ISmsSubmitHourReportService smsSubmitHourReportService;

    /**
     * 提交报表小时数（离线）
     */
    private static final int            SMS_SUBMIT_REPORT_HOURS = 72;

    @Override
    public void run(ShardingContext shardingContext) {
        try {
            long startTime = System.currentTimeMillis();
            int count = smsSubmitHourReportService.beBornSubmitHourReport(SMS_SUBMIT_REPORT_HOURS);
            logger.info("短信发送报表共生成数据：{} 条，耗时：{} ms", count, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            logger.error("生成短信发送报表失败", e);
        }
    }
}
