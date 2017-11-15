package com.huashi.bill.product.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 产品
 * @author ym
 * @created_at 2016年7月12日下午3:58:26
 */
public class Product implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6356490710239951847L;
	
	
	private int id;
	
	private String name;
	
	private int type;
	
	private int amount;
	
	private int sort;
	
	private int status;
	
	private double money;
	
	private String unit;
	
	private int operationId;
	
	private int modifyOperationId;
	
	private String remark;
	
	private Date createTime;
	
	private Date updateTime;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getOperationId() {
		return operationId;
	}

	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}

	public int getModifyOperationId() {
		return modifyOperationId;
	}

	public void setModifyOperationId(int modifyOperationId) {
		this.modifyOperationId = modifyOperationId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	

}
