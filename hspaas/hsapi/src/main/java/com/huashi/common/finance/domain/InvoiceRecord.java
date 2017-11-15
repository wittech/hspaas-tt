package com.huashi.common.finance.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.common.user.domain.User;

public class InvoiceRecord implements Serializable {
	private static final long serialVersionUID = -8536614124933729501L;

	private Integer id;

	private String title;

	private Integer userId;

	private String operatorId;

	private Integer type;

	private Integer chargeType;

	private Double chargeMoney;

	private Integer status;

	private Double money;

	private String trackingNumber;

	private String express;

	private String memo;

	private String remark;

	private String address;

	private String mobile;

	private String mailMan;

	private String zipCode;

	private Date createTime;

	private Date mailingTime;

	private Date modifyTime;

	private String taxNumber;

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	private User user;
	
	// 是否需要更新余额
	private boolean isNeedUpdateBalance = true;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId == null ? null : operatorId.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getChargeType() {
		return chargeType;
	}

	public void setChargeType(Integer chargeType) {
		this.chargeType = chargeType;
	}

	public Double getChargeMoney() {
		return chargeMoney;
	}

	public void setChargeMoney(Double chargeMoney) {
		this.chargeMoney = chargeMoney;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber == null ? null : trackingNumber.trim();
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express == null ? null : express.trim();
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo == null ? null : memo.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public String getMailMan() {
		return mailMan;
	}

	public void setMailMan(String mailMan) {
		this.mailMan = mailMan == null ? null : mailMan.trim();
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode == null ? null : zipCode.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getMailingTime() {
		return mailingTime;
	}

	public void setMailingTime(Date mailingTime) {
		this.mailingTime = mailingTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isNeedUpdateBalance() {
		return isNeedUpdateBalance;
	}

	public void setNeedUpdateBalance(boolean isNeedUpdateBalance) {
		this.isNeedUpdateBalance = isNeedUpdateBalance;
	}
	
}