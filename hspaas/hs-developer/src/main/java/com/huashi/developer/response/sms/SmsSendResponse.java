package com.huashi.developer.response.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;

/**
 * 
  * TODO 短信发送回执报告
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年5月8日 上午10:34:51
 */
public class SmsSendResponse {

    /**
     * 状态码 #com.huashi.constants.OpenApiCode
     */
	private String code;
	
	/**
	 * 回执信息描述（中文）
	 */
	private String message;
	
	/**
	 * 扣费条数，一般70个字一条，具体看客户的短信首字数配置，超出70个字时按每67字一条计
	 */
	private String fee = "0";
//	private String mobile = ""; // 发送手机号
	
	/**
	 * 消息ID，用于后续的状态匹配
	 */
	private String sid = "";

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
