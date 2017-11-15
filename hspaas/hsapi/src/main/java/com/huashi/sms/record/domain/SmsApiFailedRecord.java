package com.huashi.sms.record.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.huashi.common.user.model.UserModel;

public class SmsApiFailedRecord implements Serializable {

	private static final long serialVersionUID = 492823698036473069L;

	private Long id;

	private Integer userId;

	private Integer appType;

	private Integer submitType;

	private String appKey;

	private String appSecret;

	private String mobile;

	private String timestamps;

	private String content;

	private String extNumber;

	private String attach;

	private String callback;

	private String submitUrl;

	private String ip;

	private Date createTime;

	private String respCode;

	private String remark;

	private String errorCodeText;

	private UserModel userModel;

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String[] getMobiles(){
		if(StringUtils.isNotBlank(mobile)){
			return mobile.split(",");
		}
		return null;
	}

	public String getFirstMobile(){
		String[] mobiles = getMobiles();
		if(mobiles != null && mobiles.length > 0){
			return mobiles[0];
		}
		return mobile;
	}

	public Date getSubmitTime(){
		if(StringUtils.isNotBlank(timestamps)){
			long t = Long.valueOf(timestamps);
			return new Date(t);
		}
		return null;
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

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public Integer getSubmitType() {
		return submitType;
	}

	public void setSubmitType(Integer submitType) {
		this.submitType = submitType;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey == null ? null : appKey.trim();
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret == null ? null : appSecret.trim();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public String getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(String timestamps) {
		this.timestamps = timestamps == null ? null : timestamps.trim();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public String getExtNumber() {
		return extNumber;
	}

	public void setExtNumber(String extNumber) {
		this.extNumber = extNumber == null ? null : extNumber.trim();
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach == null ? null : attach.trim();
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback == null ? null : callback.trim();
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl == null ? null : submitUrl.trim();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip == null ? null : ip.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode == null ? null : respCode.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public String getErrorCodeText() {
		return errorCodeText;
	}

	public void setErrorCodeText(String errorCodeText) {
		this.errorCodeText = errorCodeText;
	}

}