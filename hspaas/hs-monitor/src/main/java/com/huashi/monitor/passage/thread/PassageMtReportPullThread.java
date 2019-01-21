package com.huashi.monitor.passage.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.util.DateUtil;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtDeliverService;

/**
 * TODO 短信状态报告线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年6月6日 上午10:37:24
 */
public class PassageMtReportPullThread extends BaseThread implements Runnable {

    private SmsPassageAccess       smsPassageAccess;

    ISmsMtDeliverService           smsMtDeliverService;
    private ISmsProviderService    smsProviderService;
    private IPassageMonitorService passageMonitorService;

    // 单次组大小
    // private static final int GROUP_SIZE_BY_SINGLE = 500;

    public PassageMtReportPullThread(SmsPassageAccess smsPassageAccess, ISmsMtDeliverService smsMtDeliverService,
                                     ISmsProviderService smsProviderService,
                                     IPassageMonitorService passageMonitorService) {
        this.smsPassageAccess = smsPassageAccess;
        this.smsMtDeliverService = smsMtDeliverService;
        this.smsProviderService = smsProviderService;
        this.passageMonitorService = passageMonitorService;
    }

    @Override
    public void run() {
        String key = Thread.currentThread().getName();
        Boolean isGo = PASSAGES_IN_RUNNING.get(key);
        if (isGo == null) {
            logger.warn("下行状态通道线程名称：{} 数据为空", key);
            return;
        }

        if (!isGo) {
            logger.warn("下行状态通道线程名称：{} 线程终止", key);
            return;
        }

        if (!isServiceAvaiable()) {
            return;
        }

        while (PASSAGES_IN_RUNNING.get(key)) {
            try {
                // 睡眠时间
                int intevel = getSleepTime(smsPassageAccess);
                long start = System.currentTimeMillis();
                List<SmsMtMessageDeliver> list = smsProviderService.pullMtReport(smsPassageAccess);
                long costTime = System.currentTimeMillis() - start;
                if (CollectionUtils.isNotEmpty(list)) {
                    logger.info("通道轮训状态报告回执信息共获取{}条", list.size());

                    int avaiableCount = 0;
                    // int times = 0;
                    // if (list.size() > GROUP_SIZE_BY_SINGLE) {
                    // List<List<SmsMtMessageDeliver>> groupList = reGroup(list, GROUP_SIZE_BY_SINGLE);
                    // for (List<SmsMtMessageDeliver> glist : groupList) {
                    // avaiableCount += smsMtDeliverService.doFinishDeliver(glist);
                    // times++;
                    // }
                    //
                    // } else {
                    // avaiableCount = smsMtDeliverService.doFinishDeliver(list);
                    // times++;
                    // }

                    avaiableCount = smsMtDeliverService.doFinishDeliver(list);

                    // logger.info("通道轮训状态报告共处理有效数据：{}条，此次耗费 {}次", avaiableCount, times + 1);
                    PassagePullReport report = new PassagePullReport();
                    report.setIntevel(intevel);
                    report.setLastTime(DateUtil.getNow());
                    report.setLastAmount(list.size());
                    report.setCostTime(costTime);
                    report.setPullAvaiableTimes(avaiableCount);

                    passageMonitorService.updatePullReportToRedis(key, report);
                } else {
                    // logger.info("通道轮训上行回执信息无数据");
                    // continue;
                }

                Thread.sleep(intevel);

            } catch (Exception e) {
                logger.error("通道获取下行状态处理失败", e);
            }
        }
    }

    /**
     * TODO 获取间隔睡眠时间
     * 
     * @param smsPassageAccess
     * @return
     */
    private int getSleepTime(SmsPassageAccess smsPassageAccess) {
        if (smsPassageAccess == null || StringUtils.isEmpty(smsPassageAccess.getParams())) {
            return SLEEP_TIME;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(smsPassageAccess.getParams());
            String str = jsonObject.getString(INTERVAL_KEY);
            if (StringUtils.isEmpty(str)) {
                return SLEEP_TIME;
            }

            return Integer.parseInt(str);

        } catch (Exception e) {
            logger.warn("通道解析间隔时间失败", e);
            return SLEEP_TIME;
        }
    }

    @Override
    protected boolean isRemoteServiceMissed() {
        return smsMtDeliverService == null || smsProviderService == null || passageMonitorService == null;
    }

    /**
     * TODO 数据重新分组
     * 
     * @param list
     * @param pageSize
     * @return
     */
    public static List<List<SmsMtMessageDeliver>> reGroup(List<SmsMtMessageDeliver> list, int pageSize) {
        int totalCount = list.size();
        int pageCount;
        int m = totalCount % pageSize;

        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }

        List<List<SmsMtMessageDeliver>> totalList = new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            if (m == 0) {
                List<SmsMtMessageDeliver> subList = list.subList((i - 1) * pageSize, pageSize * (i));
                totalList.add(subList);
            } else {
                if (i == pageCount) {
                    List<SmsMtMessageDeliver> subList = list.subList((i - 1) * pageSize, totalCount);
                    totalList.add(subList);
                } else {
                    List<SmsMtMessageDeliver> subList = list.subList((i - 1) * pageSize, pageSize * i);
                    totalList.add(subList);
                }
            }
        }

        return totalList;
    }
}
