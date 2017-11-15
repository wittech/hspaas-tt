package com.huashi.developer.response.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;

public class SmsSendResponse {

	private String code; // 状态码
	private String message; // 成功发送的短信计费条数
	private String fee = "0"; // 扣费条数，70个字一条，超出70个字时按每67字一条计
//	private String mobile = ""; // 发送手机号
	private String sid = ""; // 消息ID

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public SmsSendResponse() {
		super();
	}

	public SmsSendResponse(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public SmsSendResponse(CommonApiCode api) {
		super();
		this.code = api.getCode();
		this.message = api.getMessage();
	}
	
	public SmsSendResponse(JSONObject jsonObject) {
		super();
		try {
			this.code = jsonObject.getString("code");
			this.message = jsonObject.getString("message");
		} catch (Exception e) {
			this.code = CommonApiCode.COMMON_SERVER_EXCEPTION.getCode();
			this.message = CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage();
		}
	}
	
	// 成功回执
	public SmsSendResponse(int fee, long sid) {
		super();
		this.code = CommonApiCode.COMMON_SUCCESS.getCode();
		this.message = CommonApiCode.COMMON_SUCCESS.getMessage();
		this.fee = fee + "";
		this.sid = sid + "";
	}

}
