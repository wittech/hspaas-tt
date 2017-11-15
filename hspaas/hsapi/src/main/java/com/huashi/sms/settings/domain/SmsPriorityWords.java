package com.huashi.sms.settings.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.common.user.model.UserModel;

public class SmsPriorityWords implements Serializable{
	
	private static final long serialVersionUID = -9046904277155085462L;

	private Integer id;

	private Integer userId;

	private String content;

	private Integer priority;

	private Integer status;

	private Date createTime;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

}