package com.huashi.sms.task.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * 
 * TODO 上行推送记录[华时平台推送给用户]
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月1日 下午2:05:20
 */
public class SmsMoPushResponse implements Serializable {

	private static final long serialVersionUID = -7688214202150384860L;
	private String receiveTime; // 如果为空则默认当前时间
	private String message; // 信息
	private String mobile = ""; // 接收手机号
	private String destPhone; // 服务号 10690号码
	private String sid; // 消息ID

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public SmsMoPushResponse() {
		super();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDestPhone() {
		return destPhone;
	}

	public void setDestPhone(String destPhone) {
		this.destPhone = destPhone;
	}

}
