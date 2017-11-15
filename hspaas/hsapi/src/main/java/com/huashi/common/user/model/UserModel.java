package com.huashi.common.user.model;

import java.io.Serializable;

public class UserModel implements Serializable {

	private static final long serialVersionUID = 5532786950934160577L;
	private int userId;
	// 开发者编号
	private String appkey;
	// 开发者密码
	private String appsecret;
	// 用户账号
	private String username;
	private String mobile;
	// 用户邮箱
	private String email;
	// 用户姓名/公司名称
	private String name;
	// 状态
	private Integer status;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserModel(int userId, String appkey, String appsecret, String username, String mobile, String email,
			String name, Integer status) {
		super();
		this.userId = userId;
		this.appkey = appkey;
		this.appsecret = appsecret;
		this.username = username;
		this.mobile = mobile;
		this.email = email;
		this.name = name;
		this.status = status;
	}

	public UserModel() {
		super();
	}
	
	public UserModel(int userId, String appkey, String name) {
		super();
		this.userId = userId;
		this.appkey = appkey;
		this.name = name;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
