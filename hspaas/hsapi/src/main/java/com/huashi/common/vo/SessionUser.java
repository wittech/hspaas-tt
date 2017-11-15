package com.huashi.common.vo;

import java.io.Serializable;

public class SessionUser implements Serializable{
	
	private static final long serialVersionUID = -768419117101419854L;

	private int id;
	private String email;
	private String mobile;
	
	// 公司名称
	private String company;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public SessionUser(int id, String email, String mobile) {
		super();
		this.id = id;
		this.email = email;
		this.mobile = mobile;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
