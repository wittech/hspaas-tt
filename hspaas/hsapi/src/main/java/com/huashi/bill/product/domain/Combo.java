package com.huashi.bill.product.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 套餐
 * @author ym
 * @created_at 2016年7月12日下午4:02:46
 */
public class Combo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4815204827668692348L;
	
	private int id;
	
	private String name;
	
	private int status;
	
	private double originalMoney;   //原价
	
	private double sellMoney;    //现价
	
	private int sort;
	
	private int operatorId;
	
	private int modifyOperatorId;
	
	private String description;
	
	private String remark;
	
	private int isInTime;    //是否包含有效时间
	
	private Date createTime;
	
	private Date modifyTime;
	
	private Date startTime;
	
	private Date endTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getOriginalMoney() {
		return originalMoney;
	}

	public void setOriginalMoney(double originalMoney) {
		this.originalMoney = originalMoney;
	}

	public double getSellMoney() {
		return sellMoney;
	}

	public void setSellMoney(double sellMoney) {
		this.sellMoney = sellMoney;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getModifyOperatorId() {
		return modifyOperatorId;
	}

	public void setModifyOperatorId(int modifyOperatorId) {
		this.modifyOperatorId = modifyOperatorId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getIsInTime() {
		return isInTime;
	}

	public void setIsInTime(int isInTime) {
		this.isInTime = isInTime;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	} 
	
	

}
