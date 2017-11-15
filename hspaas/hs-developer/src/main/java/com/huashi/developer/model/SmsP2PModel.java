package com.huashi.developer.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.developer.validator.ValidateField;

/**
 * 
 * TODO 普通点对点短信MODEL
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年3月31日 下午9:39:29
 */
public class SmsP2PModel extends BaseModel {

	private static final long serialVersionUID = -6582377105792655104L;

	// 报文信息
	@ValidateField(value = "body", utf8 = true, notEmpty = true)
	private String body;

	// 扩展码号
	@ValidateField(value = "extNumber", necessary = false, number = true)
	private String extNumber;

	// 备选:主要用于用户自定义内容，会原封不动的给用户返回（业务主要用于渠道区分自己的客户，可能传递的是渠道方自己的用户Id）
	@ValidateField(value = "attach", necessary = false)
	private String attach;

	// 回调URL
	@ValidateField(value = "callback", necessary = false)
	private String callback;

	private List<JSONObject> p2pBodies;

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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<JSONObject> getP2pBodies() {
		return p2pBodies;
	}

	public void setP2pBodies(List<JSONObject> p2pBodies) {
		this.p2pBodies = p2pBodies;
	}

}
