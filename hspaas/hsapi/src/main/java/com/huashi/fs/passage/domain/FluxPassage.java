package com.huashi.fs.passage.domain;

import java.io.Serializable;
import java.util.Date;

public class FluxPassage implements Serializable{
	private static final long serialVersionUID = 6260399420581053339L;

	private Integer id;

    private String name;

    private String code;

    private Double discount;

    private String agentName;

    private Integer payType;

    private Integer passageType;

    private Integer scope;

    private Integer provinceCode;

    private Integer operator;

    private Integer submitInterval;

    private Integer priority;

    private Integer scanInterval;

    private Integer status;

    private String remark;

    private Integer limitSize;

    private Double balance;

    private Date createTime;

    private Date modifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName == null ? null : agentName.trim();
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPassageType() {
        return passageType;
    }

    public void setPassageType(Integer passageType) {
        this.passageType = passageType;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getSubmitInterval() {
        return submitInterval;
    }

    public void setSubmitInterval(Integer submitInterval) {
        this.submitInterval = submitInterval;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(Integer scanInterval) {
        this.scanInterval = scanInterval;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

	@Override
	public String toString() {
		return "FluxPassage [id=" + id + ", name=" + name + ", code=" + code + ", discount=" + discount + ", agentName="
				+ agentName + ", payType=" + payType + ", passageType=" + passageType + ", scope=" + scope
				+ ", provinceCode=" + provinceCode + ", operator=" + operator + ", submitInterval=" + submitInterval
				+ ", priority=" + priority + ", scanInterval=" + scanInterval + ", status=" + status + ", remark="
				+ remark + ", limitSize=" + limitSize + ", balance=" + balance + ", createTime=" + createTime
				+ ", modifyTime=" + modifyTime + "]";
	}
    
    
}