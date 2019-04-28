package com.huashi.monitor.job.sms;

import java.util.Date;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.huashi.common.util.DateUtil;
import com.huashi.monitor.config.redis.MonitorRedisConstant;
import com.huashi.monitor.job.AbstractJob;
import com.huashi.monitor.passage.model.PassageReachRateReport;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.task.context.TaskContext;

/**
 * 监控短信回执率告警
 *
 * @author yangmeng
 * @version V1.0.0
 * @date 2017年4月5日 下午9:34:08
 */
public class SmsPassageReachRateReportJob extends AbstractJob {

    private ISmsMtSubmitService               smsMtSubmitService;
    private ISmsPassageService                smsPassageService;
    private StringRedisTemplate               stringRedisTemplate;
    private List<SmsPassageReachrateSettings> list;

    public SmsPassageReachRateReportJob(ISmsMtSubmitService smsMtSubmitService, ISmsPassageService smsPassageService,
                                        StringRedisTemplate stringRedisTemplate, List<SmsPassageReachrateSettings> list) {
        this.smsMtSubmitService = smsMtSubmitService;
        this.smsPassageService = smsPassageService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.list = list;
    }

    /**
     * 停止状态
     */
    private static final int    STOP_STATUS                 = 2;

    /**
     * 通道到达率模板内容（用于告警发送）
     */
    private static final String PASSAGE_REACH_RATE_TEMPLATE = "【华时科技】%s至%s %s，发送%d条，未成功%d条，成功率%.2f%%";

    /**
     * 获取需要发送内容
     * 
     * @param startTime 开始时间
     * @param stopTime 截止时间
     * @param passageName 通道名称
     * @param totalNum 总条量
     * @param failedNum 失败条数
     * @param reachRate 到达率
     * @return 短信内容
     */
    private static String getSendContent(String startTime, String stopTime, String passageName, int totalNum,
                                         int failedNum, double reachRate) {
        return String.format(PASSAGE_REACH_RATE_TEMPLATE, startTime, stopTime, passageName, totalNum, failedNum,
                             reachRate);
    }

    @Override
    public void run(ShardingContext context) {
        // if (StringUtils.isEmpty(context.getJobParameter())) {
        // logger.warn("无法识别通道信息");
        // return;
        // }

        for (SmsPassageReachrateSettings smsPassageReachrateSettings : list) {

            try {
                // 禁用的轮询 直接跳出
                if (smsPassageReachrateSettings.getStatus() == STOP_STATUS) {
                    return;
                }

                long passageId = smsPassageReachrateSettings.getPassageId();
                long startTime = smsPassageReachrateSettings.getSelectStartTime();
                long endTime = smsPassageReachrateSettings.getSelectEndTime();

                List<SmsMtMessageSubmit> smsSubmitList = smsMtSubmitService.getRecordListToMonitor(passageId,
                                                                                                   startTime, endTime);
                int totalCount = smsSubmitList.size();
                int successCount = 0;
                int failCount = 0;

                // 统计的总量大于等于设置告警阀值则进行判断 edit by 20170702
                if (totalCount >= smsPassageReachrateSettings.getCountPoint()) {
                    boolean isAlarm = false;

                    for (SmsMtMessageSubmit submit : smsSubmitList) {
                        SmsMtMessageDeliver deliver = submit.getMessageDeliver();
                        if (deliver != null && deliver.getStatus() != null
                            && deliver.getStatus() == TaskContext.MessageSubmitStatus.SUCCESS.getCode()) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    }

                    SmsPassage passage = smsPassageService.findById(Integer.parseInt(passageId + ""));
                    double successRate = (double) successCount / totalCount;

                    // 成功率小于等于设置的告警阀值进行告警短信
                    if (successRate <= smsPassageReachrateSettings.getSuccessRate()) {
                        isAlarm = true;
                        try {
                            String content = getSendContent(DateUtil.getSecondOnlyStr(new Date(startTime)),
                                                            DateUtil.getSecondOnlyStr(new Date(endTime)),
                                                            passage.getName(), totalCount, failCount,
                                                            (successRate * 100));

                            boolean result = smsPassageService.doMonitorSmsSend(smsPassageReachrateSettings.getMobile(),
                                                                                content);

                            // 打印日志
                            logger.info("告警短息发送结果：{}, 短信内容：{}， 接收手机号码：{}", result, content,
                                        smsPassageReachrateSettings.getMobile());

                        } catch (Exception e) {
                            logger.error("告警短息发送异常", e);
                        }
                    }

                    pushReportToRedis(passageId, passage.getName(), successRate,
                                      smsPassageReachrateSettings.getSuccessRate(), startTime, isAlarm);
                }
            } catch (Exception e) {
                logger.error("短信到达率统计JOB[" + context.getJobParameter() + "]异常", e);
            }
        }

    }

    /**
     * 加入本次报告至REDIS中
     * 
     * @param passageId 通道ID
     * @param passageName 通道名称
     * @param successRate 成功率
     * @param expectRate 预设成功率
     * @param statTimeMillis 开始时间
     * @param isSms 是否发送短信
     */
    private void pushReportToRedis(Long passageId, String passageName, double successRate, double expectRate,
                                   long statTimeMillis, boolean isSms) {
        try {
            PassageReachRateReport report = new PassageReachRateReport(passageId, passageName, successRate * 100,
                                                                       expectRate * 100,
                                                                       DateUtil.getSecondDateStr(statTimeMillis),
                                                                       statTimeMillis, isSms);

            stringRedisTemplate.opsForZSet().add(String.format("%s:%d",
                                                               MonitorRedisConstant.RED_PASSAGE_REACHRATE_REPORT,
                                                               passageId), JSON.toJSONString(report), statTimeMillis);

        } catch (Exception e) {
            logger.warn("通道到达率入REDIS失败", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(String.format("%.2f", 3d / 7d));
    }
}
