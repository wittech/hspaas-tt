package com.huashi.developer.model.sms;

import com.huashi.developer.model.PassportModel;
import com.huashi.developer.validator.annotation.ValidateField;

/**
 * 
  * 单条/批量短信MODEL
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年3月31日 下午9:39:53
 */
public class SmsSendRequest extends PassportModel {

	private static final long serialVersionUID = 2029866580659952586L;

	/**
	 * 手机号码
	 */
	@ValidateField(value = "mobile", required = true)
	private String mobile;

	/**
	 * 短信内容
	 */
	@ValidateField(value = "content", required = true, utf8 = true)
	private String content;

	/**
	 * 扩展码号
	 */
	@ValidateField(value = "extNumber", number = true)
	private String extNumber;

	/**
	 * 用于调用者自定义内容，主要为了实现调用侧的自行业务逻辑标识，如调用方自己的 会员标识，最终我方会原样返回
	 */
	@ValidateField(value = "attach")
	private String attach;

	/**
	 * 回调URL
	 */
	@ValidateField(value = "callback")
	private String callback;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getExtNumber() {
		return extNumber;
	}

	public void setExtNumber(String extNumber) {
		this.extNumber = extNumber;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

}
