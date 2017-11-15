package com.huashi.sms.record.model;

import java.io.Serializable;

/**
 * 
 * TODO 下行状态报告待推送数据
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年12月21日 下午8:29:38
 */
public class SmsMtWaitPushModel implements Serializable {

	private static final long serialVersionUID = -2097074493447655006L;
	private String pushUrl;
	private Integer retryTimes;
	private String report;

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}
	
	class MtPushResonpse {
		
		
	}

}
