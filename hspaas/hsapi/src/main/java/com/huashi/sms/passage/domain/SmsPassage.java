package com.huashi.sms.passage.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsPassage implements Serializable {

	private static final long serialVersionUID = 7541639625689763922L;

	private Integer id;

	private String name;

	private String code;

	private Integer cmcp;

	private Integer wordNumber;

	private Integer priority;

	private Integer hspaasTemplateId;

	private Integer status;

	private String remark;

	private Integer type;

	private Integer exclusiveUserId;

	private Integer signMode;

	private String accessCode;

	private String account;

	private Integer payType;

	private Integer balance;

	private Integer mobileSize;

	private Integer packetsSize;

	// add by 20170816 最大连接数
	private Integer connectionSize;
	
	// add by 20170831 请求响应超时时间（毫秒）
	private Integer readTimeout;

	private Integer extNumber;
	
	// 统计落地时限（单位：小时）
	private Integer bornTerm; 
	
	// 是否需要通道方短信模板参数
	private Integer smsTemplateParam;

	private Date createTime;

	private Date modifyTime;

	private List<SmsPassageParameter> parameterList = new ArrayList<SmsPassageParameter>();

	private List<SmsPassageProvince> provinceList = new ArrayList<SmsPassageProvince>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code == null ? null : code.trim();
	}

	public Integer getCmcp() {
		return cmcp;
	}

	public void setCmcp(Integer cmcp) {
		this.cmcp = cmcp;
	}

	public Integer getWordNumber() {
		return wordNumber;
	}

	public void setWordNumber(Integer wordNumber) {
		this.wordNumber = wordNumber;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getHspaasTemplateId() {
		return hspaasTemplateId;
	}

	public void setHspaasTemplateId(Integer hspaasTemplateId) {
		this.hspaasTemplateId = hspaasTemplateId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getExclusiveUserId() {
		return exclusiveUserId;
	}

	public void setExclusiveUserId(Integer exclusiveUserId) {
		this.exclusiveUserId = exclusiveUserId;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode == null ? null : accessCode.trim();
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account == null ? null : account.trim();
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public Integer getMobileSize() {
		return mobileSize;
	}

	public void setMobileSize(Integer mobileSize) {
		this.mobileSize = mobileSize;
	}

	public Integer getPacketsSize() {
		return packetsSize;
	}

	public void setPacketsSize(Integer packetsSize) {
		this.packetsSize = packetsSize;
	}

	public Integer getExtNumber() {
		return extNumber;
	}

	public void setExtNumber(Integer extNumber) {
		this.extNumber = extNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public List<SmsPassageParameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<SmsPassageParameter> parameterList) {
		this.parameterList = parameterList;
	}

	public List<SmsPassageProvince> getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List<SmsPassageProvince> provinceList) {
		this.provinceList = provinceList;
	}

	public Integer getSignMode() {
		return signMode;
	}

	public void setSignMode(Integer signMode) {
		this.signMode = signMode;
	}

	public Integer getConnectionSize() {
		return connectionSize;
	}

	public void setConnectionSize(Integer connectionSize) {
		this.connectionSize = connectionSize;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Integer getBornTerm() {
		return bornTerm;
	}

	public void setBornTerm(Integer bornTerm) {
		this.bornTerm = bornTerm;
	}

	public Integer getSmsTemplateParam() {
		return smsTemplateParam;
	}

	public void setSmsTemplateParam(Integer smsTemplateParam) {
		this.smsTemplateParam = smsTemplateParam;
	}
	
}