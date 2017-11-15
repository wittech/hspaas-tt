package com.huashi.monitor.passage.model;

import java.io.Serializable;

import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.monitor.constant.MonitorPassageContext.PassagePullRunnintStatus;

/**
 * 
 * TODO 监管中通道网络轮训获取统计数据
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年2月22日 下午8:19:30
 */
public class PassagePullReport implements Serializable {

	private static final long serialVersionUID = 389960247494057011L;
	// 通道ID
	private Integer passageId;
	// 通道调用类型（下行状态报告，上行回执等）@enum PassageCallType
	private Integer callType;
	String callTypeText;
	// 通道代码
	private String passageCode;
	// 请求用户名
	private String account;
	// 线程轮训间隔时间
	private Integer intevel;
	// 最后一次执行时间
	private String lastTime;
	// 最后一次获取总量
	private Integer lastAmount;
	// 方法执行耗时（毫秒）
	private long costTime;
	// 抓取有效总次数
	private Integer pullAvaiableTimes;
	// 状态
	private int status = PassagePullRunnintStatus.YES.getCode();
	String statusText;

	public Integer getPassageId() {
		return passageId;
	}

	public void setPassageId(Integer passageId) {
		this.passageId = passageId;
	}

	public Integer getCallType() {
		return callType;
	}

	public void setCallType(Integer callType) {
		this.callType = callType;
	}

	public String getPassageCode() {
		return passageCode;
	}

	public void setPassageCode(String passageCode) {
		this.passageCode = passageCode;
	}

	public Integer getIntevel() {
		return intevel;
	}

	public void setIntevel(Integer intevel) {
		this.intevel = intevel;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public Integer getLastAmount() {
		return lastAmount;
	}

	public void setLastAmount(Integer lastAmount) {
		this.lastAmount = lastAmount;
	}

	public long getCostTime() {
		return costTime;
	}

	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getPullAvaiableTimes() {
		return pullAvaiableTimes;
	}

	public void setPullAvaiableTimes(Integer pullAvaiableTimes) {
		this.pullAvaiableTimes = pullAvaiableTimes;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCallTypeText() {
		return PassageCallType.parse(callType) == null ? "未知" : PassageCallType.parse(callType).getName();
	}

	public String getStatusText() {
		return PassagePullRunnintStatus.parse(status);
	}

}
