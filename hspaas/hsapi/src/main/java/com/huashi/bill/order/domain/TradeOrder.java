package com.huashi.bill.order.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * TODO 交易订单
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月18日 下午2:52:03
 */
public class TradeOrder implements Serializable {

	private static final long serialVersionUID = -4993907643454167864L;

	private Long id;

	private String orderNo;

	private String tradeNo;

	private Integer userId;

	private Boolean invoiceFlag;

	private Integer tradeType;

	private Integer appType;

	private Integer productComboId;

	private String productName;

	private Double productFee;

	private Integer payType;

	private Integer status;

	private String message;

	private Date createTime;

	private Date payTime;

	private Date completeTime;

	private String remark;

	private String userFullName;

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo == null ? null : orderNo.trim();
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo == null ? null : tradeNo.trim();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Boolean getInvoiceFlag() {
		return invoiceFlag;
	}

	public void setInvoiceFlag(Boolean invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public Integer getProductComboId() {
		return productComboId;
	}

	public void setProductComboId(Integer productComboId) {
		this.productComboId = productComboId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName == null ? null : productName.trim();
	}

	public Double getProductFee() {
		return productFee;
	}

	public void setProductFee(Double productFee) {
		this.productFee = productFee;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message == null ? null : message.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}
}