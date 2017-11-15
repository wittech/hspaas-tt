package com.huashi.monitor.passage.model;

import java.io.Serializable;

/**
 * 
  * TODO 通道到达率报告
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年8月3日 下午2:13:01
 */
public class PassageReachRateReport implements Serializable{

	private static final long serialVersionUID = 2998312111686089923L;

	// 通道ID
	private Long passageId;
	
	// 通道名称
	private String passageName;
	
	// 通道成功率
	private double successRate;
	
	// 期望成功率
	private double expectRate;
	
	// 统计时间
	private String statTime;
	
	// 统计时间毫秒值
	private long statTimeMillis;
	
	// 是否发送短信
	private boolean isAlarm;

	public Long getPassageId() {
		return passageId;
	}

	public void setPassageId(Long passageId) {
		this.passageId = passageId;
	}

	public String getPassageName() {
		return passageName;
	}

	public void setPassageName(String passageName) {
		this.passageName = passageName;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}

	public double getExpectRate() {
		return expectRate;
	}

	public void setExpectRate(double expectRate) {
		this.expectRate = expectRate;
	}

	public String getStatTime() {
		return statTime;
	}

	public void setStatTime(String statTime) {
		this.statTime = statTime;
	}

	public long getStatTimeMillis() {
		return statTimeMillis;
	}

	public void setStatTimeMillis(long statTimeMillis) {
		this.statTimeMillis = statTimeMillis;
	}

	public boolean isAlarm() {
		return isAlarm;
	}

	public void setAlarm(boolean isAlarm) {
		this.isAlarm = isAlarm;
	}

	public PassageReachRateReport(Long passageId, String passageName,
			double successRate, double expectRate, String statTime,
			long statTimeMillis, boolean isAlarm) {
		super();
		this.passageId = passageId;
		this.passageName = passageName;
		this.successRate = successRate;
		this.expectRate = expectRate;
		this.statTime = statTime;
		this.statTimeMillis = statTimeMillis;
		this.isAlarm = isAlarm;
	}

	public PassageReachRateReport() {
		super();
	}
	
}
