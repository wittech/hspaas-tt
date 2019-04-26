package com.huashi.monitor.job.mms;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.huashi.common.util.DateUtil;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.service.IMmsMoMessageService;
import com.huashi.monitor.job.AbstractJob;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.service.IPassageMonitorService;

public class MmsPassageMoReportPullJob extends AbstractJob {

    private IMmsMoMessageService   mmsMoMessageService;
    private IMmsProviderService    mmsProviderService;
    private IPassageMonitorService passageMonitorService;

    public MmsPassageMoReportPullJob(IMmsMoMessageService mmsMoMessageService, IMmsProviderService mmsProviderService,
                                     IPassageMonitorService passageMonitorService) {
        super();
        this.mmsMoMessageService = mmsMoMessageService;
        this.mmsProviderService = mmsProviderService;
        this.passageMonitorService = passageMonitorService;
    }

    @Override
    public void run(ShardingContext context) {
        if (StringUtils.isEmpty(context.getJobParameter())) {
            logger.warn("无法识别通道信息");
            return;
        }

        try {
            MmsPassageAccess mmsPassageAccess = JSON.parseObject(context.getJobParameter(), MmsPassageAccess.class);

            long start = System.currentTimeMillis();
            List<MmsMoMessageReceive> list = mmsProviderService.pullMoReport(mmsPassageAccess);
            long costTime = System.currentTimeMillis() - start;
            if (CollectionUtils.isNotEmpty(list)) {

                int avaiableCount = mmsMoMessageService.doFinishReceive(list);
                logger.info("彩信通道轮训上行回执信息共获取{}条，共处理有效数据{}条", list.size(), avaiableCount);

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
