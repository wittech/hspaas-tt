package com.huashi.developer.model;

import com.huashi.developer.validator.ValidateField;

public class FluxModel extends PassportModel {

	private static final long serialVersionUID = 2029866580659952586L;

	// 手机号码
	@ValidateField("mobile")
	private String mobile;

	// 短信内容
	@ValidateField(value = "content", utf8 = true)
	private String content;

	// 扩展码号
	@ValidateField(value = "ext", required = false, number = true)
	private String extNumber;

	// 备选:主要用于用户自定义内容，会原封不动的给用户返回（业务主要用于渠道区分自己的客户，可能传递的是渠道方自己的用户Id）
	@ValidateField(value = "attach", required = false)
	private String attach;

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

}
