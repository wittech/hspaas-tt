package com.huashi.common.user.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.bill.pay.constant.PayContext.PaySource;

public class UserBalance implements Serializable {

	private static final long serialVersionUID = -8492197326319323515L;

	private Integer id;

	private Integer userId;
	private String mobile;
	// 虚拟列
	private String name;

	private Integer type;

	private Double balance;
	
	// 告警阀值
	private Integer threshold;
	// 告警状态
	private Integer status;

	private Integer payType;

	private String remark;

	private Date createTime;

	private Date modifyTime;
	
    private Double price;

    private Double totalPrice;
    
    private PaySource paySource;

	public UserBalance() {
	}

	public UserBalance(Integer userId, Integer type, Double balance) {
		this.userId = userId;
		this.type = type;
		this.balance = balance;
	}

	public UserBalance(Integer userId, Integer type, Integer payType,Double balance) {
		this.userId = userId;
		this.type = type;
		this.payType = payType;
		this.balance = balance;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public PaySource getPaySource() {
		return paySource;
	}

	public void setPaySource(PaySource paySource) {
		this.paySource = paySource;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}