package com.huashi.monitor.passage.thread;

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
 * 
  * TODO 短信状态报告线程
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年6月6日 上午10:37:24
 */
public class PassageMtReportPullThread extends BaseThread implements Runnable {

	private SmsPassageAccess smsPassageAccess;
	
	ISmsMtDeliverService smsMtDeliverService;
	private ISmsProviderService smsProviderService;
	private IPassageMonitorService passageMonitorService;

	public PassageMtReportPullThread(SmsPassageAccess smsPassageAccess,
			ISmsMtDeliverService smsMtDeliverService,
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
		if(isGo == null) {
			logger.warn("下行状态通道线程名称：{} 数据为空", key);
			return;
		}
		
		if(!isGo) {
			logger.warn("下行状态通道线程名称：{} 线程终止", key);
			return;
		}
		
		if(!isServiceAvaiable()) {
            return;
        }
		
		while (PASSAGES_IN_RUNNING.get(key)) {
			try {
				// 睡眠时间
				int intevel = getSleepTime(smsPassageAccess);
				long start = System.currentTimeMillis();
				List<SmsMtMessageDeliver> list = smsProviderService.doPullStatusReport(smsPassageAccess);
				long costTime = System.currentTimeMillis() -  start;
				if (CollectionUtils.isNotEmpty(list)) {
					
					int avaiableCount = smsMtDeliverService.doFinishDeliver(list);
					logger.info("通道轮训状态报告回执信息共获取{}条，共处理有效数据：{}条", list.size(), avaiableCount);

					PassagePullReport report = new PassagePullReport();
					report.setIntevel(intevel);
					report.setLastTime(DateUtil.getNow());
					report.setLastAmount(list.size());
					report.setCostTime(costTime);
					report.setPullAvaiableTimes(avaiableCount);
					
					passageMonitorService.updatePullReportToRedis(key, report);
				} else {
//					logger.info("通道轮训上行回执信息无数据");
//					continue;
				}

				Thread.sleep(intevel);

			} catch (Exception e) {
				logger.error("通道获取下行状态处理失败", e);
			}
		}
	}

	/**
	 * 
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

}
