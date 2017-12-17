package com.huashi.sms.passage.domain;

import java.util.Date;

public class SmsPassageReachrateSettings {
	private Integer id;

	private Long passageId;

	private Long interval; // 轮询间隔（单位秒）

	private Long startTime; // 数据源时间（开始执行时间点，单位秒）

	private Long timeLength; // 数据源时长

	private Integer countPoint; // 计数阀值，起始点

	private Integer rateThreshold; // 到达率，整数，最大100

	private Date createTime;

	private Integer status; // 1-正常 2-禁用

	private String passageName;

	private String mobile; // 通知手机号码

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getInterval() {
		return interval;
	}

	public Long getShowMinuteInterval() {
		return interval / 60;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getShowMinuteStartTime() {
		return startTime / 60;
	}

	public Long getTimeLength() {
		return timeLength;
	}

	public Long getShowMinuteTimeLength() {
		return timeLength / 60;
	}

	public void setTimeLength(Long timeLength) {
		this.timeLength = timeLength;
	}

	public Integer getCountPoint() {
		return countPoint;
	}

	public void setCountPoint(Integer countPoint) {
		this.countPoint = countPoint;
	}

	public Integer getRateThreshold() {
		return rateThreshold;
	}

	public void setRateThreshold(Integer rateThreshold) {
		this.rateThreshold = rateThreshold;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public long getSelectStartTime() {
		long currentTime = System.currentTimeMillis();
		long start = startTime * 1000;
		long length = timeLength * 1000;
		long startTime = currentTime - (start + length);
		return startTime;
	}

	public long getSelectEndTime() {
		long currentTime = System.currentTimeMillis();
		long start = startTime * 1000;
		long startTime = currentTime - start;
		return startTime;
	}

	public double getSuccessRate() {
		return (double) rateThreshold / 100;
	}

	public static void main(String[] args) {
		SmsPassageReachrateSettings settings = new SmsPassageReachrateSettings();
		settings.setStartTime(300L);
		settings.setTimeLength(300L);
		settings.setCountPoint(10);
		settings.setRateThreshold(67);

		System.out.println("start: " + settings.getSelectStartTime());
		System.out.println("end: " + settings.getSelectEndTime());

		int totalCount = 69;

		System.out.println("阀值" + settings.getSuccessRate());

		// 统计的总量大于等于设置告警阀值则进行判断 edit by 20170702
		if (totalCount >= settings.getCountPoint()) {
			// for (SmsMtMessageSubmit submit : smsSubmitList) {
			// SmsMtMessageDeliver deliver = submit.getMessageDeliver();
			// if (deliver != null && deliver.getStatus() != null &&
			// deliver.getStatus() ==
			// TaskContext.MessageSubmitStatus.SUCCESS.getCode())
			// successCount++;
			// else
			// failCount++;
			// }

			int successCount = 69;

			double successRate = (double) successCount / totalCount;

			// 成功率小于等于设置的告警阀值进行告警短信
			if (successRate < settings.getSuccessRate()) {

				// boolean result =
				// smsPassageService.doMonitorSmsSend(smsPassageReachrateSettings.getMobile(),
				// getSendContent(DateUtil.getSecondDateStr(startTime),
				// DateUtil.getSecondDateStr(endTime),
				// passage.getName(), totalCount, failCount, (successRate *
				// 100)));
				// if (!result) {
				// LOG.info("发送告警短信失败！");
				// }

				System.out.println("发送了");
			}

		}
	}

}