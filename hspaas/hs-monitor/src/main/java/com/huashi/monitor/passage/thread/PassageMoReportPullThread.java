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
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.service.ISmsMoMessageService;

public class PassageMoReportPullThread extends BaseThread implements Runnable {

	private SmsPassageAccess smsPassageAccess;
	private ISmsMoMessageService smsMoMessageService;
	private ISmsProviderService smsProviderService;
	private IPassageMonitorService passageMonitorService;

	public PassageMoReportPullThread(SmsPassageAccess smsPassageAccess,
			ISmsMoMessageService smsMoMessageService,
			ISmsProviderService smsProviderService,
			IPassageMonitorService passageMonitorService) {
		this.smsPassageAccess = smsPassageAccess;
		this.smsMoMessageService = smsMoMessageService;
		this.smsProviderService = smsProviderService;
		this.passageMonitorService = passageMonitorService;
	}

	public void run() {
		String key = Thread.currentThread().getName();
		Boolean isGo = PASSAGES_IN_RUNNING.get(key);
		if(isGo == null) {
			logger.warn("上行报告通道线程名称：{} 数据为空", key);
			return;
		}
		
		if(!isGo) {
			logger.warn("上行报告通道线程名称：{} 线程终止", key);
			return;
		}
		
		if(!isServiceAvaiable())
			return;
		
		while (PASSAGES_IN_RUNNING.get(key)) {
			try {
				// 睡眠时间
				int intevel = getSleepTime(smsPassageAccess);
				long start = System.currentTimeMillis();
				List<SmsMoMessageReceive> list = smsProviderService.doPullMoReport(smsPassageAccess);
				long costTime = System.currentTimeMillis() -  start;
				if (CollectionUtils.isNotEmpty(list)) {
					
					int avaiableCount = smsMoMessageService.doFinishReceive(list);
					logger.info("通道轮训上行回执信息共获取{}条，共处理有效数据{}条", list.size(), avaiableCount);

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
				logger.error("通道获取上行处理失败", e);
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
		if (smsPassageAccess == null || StringUtils.isEmpty(smsPassageAccess.getParams()))
			return SLEEP_TIME;

		try {
			JSONObject jsonObject = JSON.parseObject(smsPassageAccess.getParams());
			String str = jsonObject.getString(INTERVAL_KEY);
			if (StringUtils.isEmpty(str))
				return SLEEP_TIME;

			return Integer.parseInt(str);

		} catch (Exception e) {
			logger.warn("通道解析间隔时间失败，采用默认休眠轮训时间 : {} ms", SLEEP_TIME , e);
			return SLEEP_TIME;
		}
	}

	@Override
	protected boolean isRemoteServiceMissed() {
		return smsMoMessageService == null || smsProviderService == null || passageMonitorService == null;
	}

}
