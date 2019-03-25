package com.huashi.listener.task.mms;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.service.IMmsMoMessageService;

public class MmsPassageMoReportPullTask implements Runnable {

    // 默认线程休眠20秒
    private static final int     SLEEP_TIME   = 5 * 1000;
    // 自定义间隔时间
    private static final String  INTERVAL_KEY = "interval";

    private Logger               logger       = LoggerFactory.getLogger(getClass());

    private MmsPassageAccess     mmsPassageAccess;
    private IMmsMoMessageService mmsMoMessageService;
    private IMmsProviderService  mmsProviderService;

    public MmsPassageMoReportPullTask(MmsPassageAccess mmsPassageAccess, IMmsMoMessageService mmsMoMessageService,
                                      IMmsProviderService mmsProviderService) {
        this.mmsPassageAccess = mmsPassageAccess;
        this.mmsMoMessageService = mmsMoMessageService;
        this.mmsProviderService = mmsProviderService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<MmsMoMessageReceive> list = mmsProviderService.pullMoReport(mmsPassageAccess);
                if (CollectionUtils.isNotEmpty(list)) {
                    mmsMoMessageService.doFinishReceive(list);
                    logger.info("通道轮训上行回执信息共处理{}条", list.size());
                } else {
                    // logger.info("通道轮训上行回执信息无数据");
                    // continue;
                }

                Thread.sleep(getSleepTime(mmsPassageAccess));

            } catch (Exception e) {
                logger.error("通道获取上行处理失败", e);
            }
        }
    }

    /**
     * TODO 获取间隔睡眠时间
     * 
     * @param mmsPassageAccess
     * @return
     */
    private int getSleepTime(MmsPassageAccess mmsPassageAccess) {
        if (mmsPassageAccess == null || StringUtils.isEmpty(mmsPassageAccess.getParams())) {
            return SLEEP_TIME;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(mmsPassageAccess.getParams());
            String str = jsonObject.getString(INTERVAL_KEY);
            if (StringUtils.isEmpty(str)) {
                return SLEEP_TIME;
            }

            return Integer.parseInt(str);

        } catch (Exception e) {
            logger.warn("通道解析间隔时间失败，采用默认休眠轮训时间 : {} ms", SLEEP_TIME, e);
            return SLEEP_TIME;
        }
    }

}
