package com.huashi.sms.passage.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.common.user.model.UserModel;

public class SmsPassageAccess implements Serializable {

	private static final long serialVersionUID = -4609147131830713940L;

	private Integer id;

	private Integer userId;

	private Integer groupId;

	private Integer routeType;

	private Integer cmcp;

	private Integer passageId;

	private String passageCode;

	private String protocol;

	private Integer callType;

	private String url;

	private String paramsDefinition;

	private String params;

	private String resultFormat;

	private String successCode;

	private String position;

	private Integer mobileSize;

	private Integer packetsSize;
	
	private Integer connectionSize;
	
	// add by 20170831 请求响应超时时间（毫秒）
	private Integer readTimeout;

	private String accessCode;

	private Integer extNumber;

	private Integer signMode;

	private Date createTime;
	
	private Integer status;
	
	// 是否需要通道方短信模板参数
	private Integer smsTemplateParam;

	private String userIdText;
	private String passageIdText;

	private Integer provinceCode;
	private String provinceName;
	private String cmcpName;
	private String routeTypeText;
	private UserModel userModel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getRouteType() {
		return routeType;
	}

	public void setRouteType(Integer routeType) {
		this.routeType = routeType;
	}

	public Integer getCmcp() {
		return cmcp;
	}

	public void setCmcp(Integer cmcp) {
		this.cmcp = cmcp;
	}

	public Integer getPassageId() {
		return passageId;
	}

	public void setPassageId(Integer passageId) {
		this.passageId = passageId;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol == null ? null : protocol.trim();
	}

	public Integer getCallType() {
		return callType;
	}

	public void setCallType(Integer callType) {
		this.callType = callType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public String getParamsDefinition() {
		return paramsDefinition;
	}

	public void setParamsDefinition(String paramsDefinition) {
		this.paramsDefinition = paramsDefinition == null ? null : paramsDefinition.trim();
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params == null ? null : params.trim();
	}

	public String getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(String resultFormat) {
		this.resultFormat = resultFormat == null ? null : resultFormat.trim();
	}

	public String getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(String successCode) {
		this.successCode = successCode == null ? null : successCode.trim();
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position == null ? null : position.trim();
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

	public Integer getSignMode() {
		return signMode;
	}

	public void setSignMode(Integer signMode) {
		this.signMode = signMode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserIdText() {
		return userIdText;
	}

	public void setUserIdText(String userIdText) {
		this.userIdText = userIdText;
	}

	public String getPassageIdText() {
		return passageIdText;
	}

	public void setPassageIdText(String passageIdText) {
		this.passageIdText = passageIdText;
	}

	public Integer getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(Integer provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getPassageCode() {
		return passageCode;
	}

	public void setPassageCode(String passageCode) {
		this.passageCode = passageCode;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCmcpName() {
		return cmcpName;
	}

	public void setCmcpName(String cmcpName) {
		this.cmcpName = cmcpName;
	}

	public String getRouteTypeText() {
		return routeTypeText;
	}

	public void setRouteTypeText(String routeTypeText) {
		this.routeTypeText = routeTypeText;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Integer getSmsTemplateParam() {
		return smsTemplateParam;
	}

	public void setSmsTemplateParam(Integer smsTemplateParam) {
		this.smsTemplateParam = smsTemplateParam;
	}

}