package com.huashi.bill.order.model;

import java.io.Serializable;

public class TradeOrderModel implements Serializable {

	private static final long serialVersionUID = 6975336456927642417L;
	protected int userId;
	protected Integer comboId;

	// 是否需要发票
	protected Integer invoiceFlag;
	// 发票抬头
	protected String invoiceTitle;
	// 收件人/姓名
	protected String name;
	protected String mobile;
	protected String address;

	// 支付方式
	protected int payType;
	protected String operator;
	// 交易方式
	protected Integer tradeType;

	// 针对站内账户充值，无套餐ID，需设置此值
	protected String productName;
	protected String productFee;

	// 客户端调用IP
	protected String ip;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getComboId() {
		return comboId;
	}

	public void setComboId(Integer comboId) {
		this.comboId = comboId;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProductFee() {
		return productFee;
	}

	public void setProductFee(String productFee) {
		this.productFee = productFee;
	}

	public Integer getInvoiceFlag() {
		return invoiceFlag;
	}

	public void setInvoiceFlag(Integer invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}

}
