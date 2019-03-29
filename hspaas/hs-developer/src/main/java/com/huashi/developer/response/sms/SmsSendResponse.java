package com.huashi.developer.response.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
  * TODO 短信发送回执报告
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年5月8日 上午10:34:51
 */
@ApiModel("华时融合平台--短信发送返回结果")
public class SmsSendResponse {

    /**
     * 状态码 #com.huashi.constants.OpenApiCode
     */
	@ApiModelProperty(value = "code 200:登录成功 100:登录失败，服务异常 101:手机号码不存在 102:账号或密码错误，请重新输入 103:请求参数异常 104:手机号码格式错误 105:请获取短信验证码", required = true)
	private String code;
	
	/**
	 * 回执信息描述（中文）
	 */
	@ApiModelProperty(value = "消息描述", required = true)
	private String message;
	/**
	 * 扣费条数，一般70个字一条，具体看客户的短信首字数配置，超出70个字时按每67字一条计
	 */
	@ApiModelProperty(value = "扣费条数", required = true)
	private String fee = "0";
//	private String mobile = ""; // 发送手机号
	
	/**
	 * 消息ID，用于后续的状态匹配
	 */
	@ApiModelProperty(value = "消息ID(唯一)")
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
