package com.huashi.listener.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtDeliverService;

public class PassageStatusReportPullTask implements Runnable {

	// 默认线程休眠20秒
	private static final int SLEEP_TIME = 20 * 1000;
	// 自定义间隔时间
	private static final String INTERVAL_KEY = "interval";

	private Logger logger = LoggerFactory.getLogger(getClass());

	private SmsPassageAccess smsPassageAccess;
	private ISmsMtDeliverService smsMtDeliverService;
	private ISmsProviderService smsProviderService;

	public PassageStatusReportPullTask(SmsPassageAccess smsPassageAccess, ISmsMtDeliverService smsMtDeliverService,
			ISmsProviderService smsProviderService) {
		this.smsPassageAccess = smsPassageAccess;
		this.smsMtDeliverService = smsMtDeliverService;
		this.smsProviderService = smsProviderService;
	}

	public void run() {
		while (true) {
			try {
				List<SmsMtMessageDeliver> list = smsProviderService.doPullStatusReport(smsPassageAccess);
				if (CollectionUtils.isNotEmpty(list)) {
					smsMtDeliverService.doFinishDeliver(list);
					logger.info("通道轮训状态报告回执信息共处理{}条", list.size());
				} else {
//					logger.info("通道轮训状态报告回执信息无数据");
//					continue;
				}

				Thread.sleep(getSleepTime(smsPassageAccess));

			} catch (Exception e) {
				logger.warn("通道解析间隔时间失败，采用默认休眠轮训时间 : {} ms", SLEEP_TIME , e);
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
			logger.warn("通道解析间隔时间失败", e);
			return SLEEP_TIME;
		}
	}

}
