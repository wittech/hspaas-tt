package com.huashi.constants;

import java.io.Serializable;

/**
 * 
  * TODO Web相应结果
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年4月8日 下午11:22:08
 */
public class ResponseMessage implements Serializable {

	private static final long serialVersionUID = -6086322453135639350L;
	private int code = 0;
	private String message = "操作成功";
	private Boolean ok = true;
	
	public static final int ERROR_CODE = -1;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getOk() {
		return ok;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}

	public ResponseMessage(int code, String message, Boolean ok) {
		super();
		this.code = code;
		this.message = message;
		this.ok = ok;
	}
	
	public ResponseMessage(String message) {
		super();
		this.message = message;
	}

}
