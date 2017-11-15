package com.huashi.sms.record.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.common.user.model.UserModel;

public class SmsMoMessageReceive implements Serializable {

	private static final long serialVersionUID = 2028793797084343535L;

	private Long id;

	private Integer userId;

	private Integer passageId;

	private String msgId;

	private String mobile;

	private String content;

	private String destnationNo;

	private String receiveTime;

	private Date createTime;

	private Long createUnixtime;

	private String receiveTimeText;

	private String passageName;

	private UserModel userModel;
	
	private Boolean needPush = false;

	private String pushUrl;
	//短信上行推送
	private SmsMoMessagePush messagePush;
	
	private String sid;
	private Integer retryTimes;

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPassageId() {
		return passageId;
	}

	public void setPassageId(Integer passageId) {
		this.passageId = passageId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId == null ? null : msgId.trim();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public String getDestnationNo() {
		return destnationNo;
	}

	public void setDestnationNo(String destnationNo) {
		this.destnationNo = destnationNo == null ? null : destnationNo.trim();
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime == null ? null : receiveTime.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreateUnixtime() {
		return createUnixtime;
	}

	public void setCreateUnixtime(Long createUnixtime) {
		this.createUnixtime = createUnixtime;
	}

	public String getReceiveTimeText() {
		return receiveTimeText;
	}

	public void setReceiveTimeText(String receiveTimeText) {
		this.receiveTimeText = receiveTimeText;
	}

	public String getPassageName() {
		return passageName;
	}

	public void setPassageName(String passageName) {
		this.passageName = passageName;
	}

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

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public SmsMoMessagePush getMessagePush() {
		return messagePush;
	}

	public void setMessagePush(SmsMoMessagePush messagePush) {
		this.messagePush = messagePush;
	}

	public Boolean getNeedPush() {
		return needPush;
	}

	public void setNeedPush(Boolean needPush) {
		this.needPush = needPush;
	}
	
}