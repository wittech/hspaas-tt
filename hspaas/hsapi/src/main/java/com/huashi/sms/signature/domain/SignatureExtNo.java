package com.huashi.sms.signature.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.common.user.model.UserModel;

public class SignatureExtNo implements Serializable{
	
	private static final long serialVersionUID = 6197489373588986039L;

	private Long id;

	private Integer userId;

	private String signature;

	private String extNumber;

	private String remark;

	private Date createTime;

	private UserModel userModel;

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

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature == null ? null : signature.trim();
	}

	public String getExtNumber() {
		return extNumber;
	}

	public void setExtNumber(String extNumber) {
		this.extNumber = extNumber == null ? null : extNumber.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
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