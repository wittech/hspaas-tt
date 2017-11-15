package com.huashi.bill.pay.model;

import java.io.Serializable;

/**
 * 
 * TODO 支付订单参数
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月15日 上午12:03:41
 */
public class BasePayModel implements Serializable {

	private static final long serialVersionUID = -7840983620672780658L;

	// 交易支付订单号，针对重新支付，支付订单号需要重新生成
	private String tradeNo;
	// 产品名称（显示的支付项）
	private String product;
	// 支付金额，单位具体看接口方定义，如微信支付为整型：分，如1元则此值为100分
	private String totalFee;
	// 调用IP，默认系统IP即可
	private String ip;

	// 同步返回页面网址
	private String pageUrl;
	// 异步后台URL网址（用于对账）
	private String notifyUrl;


	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

}
