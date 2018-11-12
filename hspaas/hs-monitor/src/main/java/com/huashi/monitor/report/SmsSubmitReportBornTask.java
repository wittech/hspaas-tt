package com.huashi.monitor.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.sms.report.service.ISmsSubmitHourReportService;

/**
 * TODO 每小时离线生成短信提交报告数据
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年8月23日 上午11:39:32
 */
@Service
public class SmsSubmitReportBornTask {

    @Reference
    private ISmsSubmitHourReportService smsSubmitHourReportService;

    private final Logger                logger                  = LoggerFactory.getLogger(getClass());

    /**
     * 提交报表小时数（离线）
     */
    private static final int            SMS_SUBMIT_REPORT_HOURS = 72;

    /**
     * TODO 预计达到每小时第20分钟执行一次
     */
    @Scheduled(cron = "0 20 * * * ?")
    public void execute() {
        try {
            long startTime = System.currentTimeMillis();
            int count = smsSubmitHourReportService.beBornSubmitHourReport(SMS_SUBMIT_REPORT_HOURS);
            logger.info("短信发送报表共生成数据：{} 条，耗时：{} ms", count, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            logger.error("生成短信发送报表失败", e);
        }

    }

}
