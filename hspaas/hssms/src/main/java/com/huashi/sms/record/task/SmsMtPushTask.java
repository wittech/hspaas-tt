package com.huashi.sms.record.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huashi.sms.record.service.ISmsMtPushService;

public class SmsMtPushTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private String queueName;
	private ISmsMtPushService smsMtPushService;

	public SmsMtPushTask(String queueName, ISmsMtPushService smsMtPushService) {
		super();
		this.queueName = queueName;
		this.smsMtPushService = smsMtPushService;
	}

	@Override
	public void run() {
		while(true) {
			try {
				smsMtPushService.pushReportToUser(queueName);
			} catch (Exception e) {
				logger.error("推送短信状态回执报告至用户失败, 用户队列为：{}", queueName, e);
			}
		}
		
	}

}
