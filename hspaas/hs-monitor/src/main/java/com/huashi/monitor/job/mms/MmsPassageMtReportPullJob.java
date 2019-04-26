package com.huashi.monitor.job.mms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.huashi.common.util.DateUtil;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.record.service.IMmsMtDeliverService;
import com.huashi.monitor.job.AbstractJob;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * TODO 彩信状态报告线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年6月6日 上午10:37:24
 */

public class MmsPassageMtReportPullJob extends AbstractJob {

    private IMmsMtDeliverService   mmsMtDeliverService;
    private IMmsProviderService    mmsProviderService;
    private IPassageMonitorService passageMonitorService;
    
    public MmsPassageMtReportPullJob(IMmsMtDeliverService mmsMtDeliverService, IMmsProviderService mmsProviderService,
                              IPassageMonitorService passageMonitorService) {
        super();
        this.mmsMtDeliverService = mmsMtDeliverService;
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
            List<MmsMtMessageDeliver> list = mmsProviderService.pullMtReport(mmsPassageAccess);
            long costTime = System.currentTimeMillis() - start;
            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("彩信通道轮训状态报告回执信息共获取{}条", list.size());

                int avaiableCount = 0;
                // int times = 0;
                // if (list.size() > GROUP_SIZE_BY_SINGLE) {
                // List<List<SmsMtMessageDeliver>> groupList = reGroup(list, GROUP_SIZE_BY_SINGLE);
                // for (List<SmsMtMessageDeliver> glist : groupList) {
                // avaiableCount += mmsMtDeliverService.doFinishDeliver(glist);
                // times++;
                // }
                //
                // } else {
                // avaiableCount = mmsMtDeliverService.doFinishDeliver(list);
                // times++;
                // }

                avaiableCount = mmsMtDeliverService.doFinishDeliver(list);

                // logger.info("通道轮训状态报告共处理有效数据：{}条，此次耗费 {}次", avaiableCount, times + 1);
                PassagePullReport report = new PassagePullReport();
                report.setLastTime(DateUtil.getNow());
                report.setLastAmount(list.size());
                report.setCostTime(costTime);
                report.setPullAvaiableTimes(avaiableCount);

                passageMonitorService.updatePullReportToRedis(getJobDes(context), report);
            }

        } catch (Exception e) {
            logger.error("短信通道获取下行状态处理失败", e);
        }
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
