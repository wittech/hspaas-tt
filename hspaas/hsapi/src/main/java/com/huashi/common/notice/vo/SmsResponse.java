package com.huashi.common.notice.vo;

import java.io.Serializable;

public class SmsResponse implements Serializable {

	private static final long serialVersionUID = 6692759565156732009L;
	private String mobile;
	private String code;
	private String msg;

	public SmsResponse(String mobile, String code, String msg) {
		super();
		this.mobile = mobile;
		this.code = code;
		this.msg = msg;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
