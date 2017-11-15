package com.huashi.web.context;

import java.io.Serializable;

public class HttpResponse implements Serializable {

	private static final long serialVersionUID = 6507560410050654752L;
	private int code;
	private String msg;
	private boolean success;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public HttpResponse(boolean success, String msg) {
		super();
		this.success = success;
		this.msg = msg;
	}

	public HttpResponse() {
		super();
	}

	public HttpResponse(boolean success) {
		super();
		this.success = success;
	}

}
