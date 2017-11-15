package com.huashi.bill.pay.model;

/**
 * 
 * TODO 支付宝订单参数
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月15日 上午12:09:55
 */
public class AlipayModel extends BasePayModel {

	private static final long serialVersionUID = -6512776429179614133L;
	// 商品描述信息
	private String body;
	// 可以为空queryTtimestamp()
	private String antiPhishingKey = "";

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAntiPhishingKey() {
		return antiPhishingKey;
	}

	public void setAntiPhishingKey(String antiPhishingKey) {
		this.antiPhishingKey = antiPhishingKey;
	}

}
