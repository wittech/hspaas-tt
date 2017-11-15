package com.huashi.developer.model;

import java.io.Serializable;

import com.huashi.developer.validator.ValidateField;

public class BaseModel implements Serializable {

	private static final long serialVersionUID = -1657874445200036840L;

	@ValidateField("appkey")
	protected String appkey; // 接口账号

	@ValidateField("appsecret")
	protected String appsecret; // 接口密码

	@ValidateField("timestamp")
	protected String timestamp; // 时间戳

	protected int apptype;

	// 计费
	protected int fee;
	// 总计费数
	protected int totalFee;
	// 用户ID
	protected int userId;
	// 用户调用IP
	protected String ip;

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getApptype() {
		return apptype;
	}

	public void setApptype(int apptype) {
		this.apptype = apptype;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

}
