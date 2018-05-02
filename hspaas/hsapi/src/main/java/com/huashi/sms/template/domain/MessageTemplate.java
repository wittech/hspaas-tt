package com.huashi.sms.template.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.common.user.model.UserModel;
import com.huashi.sms.template.context.TemplateContext.IgnoreBlacklist;

public class MessageTemplate implements Serializable {

	private static final long serialVersionUID = 3361058935868304392L;

	private Long id;

	private Integer userId;

	private String content;

	private Integer status;

	private Integer appType;

	private Date createTime;

	private Date approveTime;

	private String approveUser;

	private String remark;

	private Integer noticeMode;

	private String mobile;

	private String regexValue;

	private Integer submitInterval;

	private Integer limitTimes;

	private String whiteWord;

	private Integer routeType;

	private Integer priority;
	
	private String extNumber;
	
	/**
	 * 忽略手机黑名单拦截 add by 2018-05-02
	 */
	private Integer ignoreBlacklist = IgnoreBlacklist.NO.getValue();

	// 用户信息
	private UserModel userModel;
	// 路由类型名称
	private String routeTypeText;
	// 平台类型名称
	private String apptypeText;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getApproveTime() {
		return approveTime;
	}

	public void setApproveTime(Date approveTime) {
		this.approveTime = approveTime;
	}

	public String getApproveUser() {
		return approveUser;
	}

	public void setApproveUser(String approveUser) {
		this.approveUser = approveUser == null ? null : approveUser.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getNoticeMode() {
		return noticeMode;
	}

	public void setNoticeMode(Integer noticeMode) {
		this.noticeMode = noticeMode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public String getRegexValue() {
		return regexValue;
	}

	public void setRegexValue(String regexValue) {
		this.regexValue = regexValue == null ? null : regexValue.trim();
	}

	public Integer getSubmitInterval() {
		return submitInterval;
	}

	public void setSubmitInterval(Integer submitInterval) {
		this.submitInterval = submitInterval;
	}

	public Integer getLimitTimes() {
		return limitTimes;
	}

	public void setLimitTimes(Integer limitTimes) {
		this.limitTimes = limitTimes;
	}

	public String getWhiteWord() {
		return whiteWord;
	}

	public void setWhiteWord(String whiteWord) {
		this.whiteWord = whiteWord == null ? null : whiteWord.trim();
	}

	public Integer getRouteType() {
		return routeType;
	}

	public void setRouteType(Integer routeType) {
		this.routeType = routeType;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getRouteTypeText() {
		return routeTypeText;
	}

	public void setRouteTypeText(String routeTypeText) {
		this.routeTypeText = routeTypeText;
	}

	public String getApptypeText() {
		return apptypeText;
	}

	public void setApptypeText(String apptypeText) {
		this.apptypeText = apptypeText;
	}

	public String getExtNumber() {
		return extNumber;
	}

	public void setExtNumber(String extNumber) {
		this.extNumber = extNumber;
	}

    
    public Integer getIgnoreBlacklist() {
        return ignoreBlacklist;
    }

    
    public void setIgnoreBlacklist(Integer ignoreBlacklist) {
        this.ignoreBlacklist = ignoreBlacklist;
    }

}