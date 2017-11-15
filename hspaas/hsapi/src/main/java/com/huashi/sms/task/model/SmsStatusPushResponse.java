package com.huashi.sms.task.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode;

/**
 * 
 * TODO 短信状态回执推送[华时平台推送至用户]
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月1日 下午1:54:00
 */
public class SmsStatusPushResponse implements Serializable {

	private static final long serialVersionUID = 6624142087649097291L;
	private String status = OpenApiCode.DELIVER_SUCCESS; // 状态码
	private String receiveTime; // 如果为空则默认当前时间
	private String errorMsg; // 错误信息
	private String mobile = ""; // 发送手机号
	private String sid; // 消息ID
	private String attach; // 自定义内容
	
	private String msgId;//网关回执消息ID（不返回给用户）
	private int retryTimes; // 重试次数

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

	public SmsStatusPushResponse() {
		super();
	}
	
	public SmsStatusPushResponse(JSONObject jsonObject) {
		this.sid = jsonObject.getString("sid");
		this.attach = jsonObject.getString("attach");
		this.receiveTime = jsonObject.getString("receiveTime");
		this.mobile = jsonObject.getString("mobile");
		this.status = jsonObject.getString("status");
		this.msgId = jsonObject.getString("msgId");
		this.retryTimes = jsonObject.getIntValue("retryTimes");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	
}