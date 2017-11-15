package com.huashi.common.finance.domain;

import java.io.Serializable;
import java.util.Date;

public class InvoiceBalance implements Serializable{
    
	private static final long serialVersionUID = 7870476223438214082L;

	private Integer id;

    private Integer userId;

    private Double money;

    private Date createTime;

    private Date modifyTime;

    private Integer modifyTimes;

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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
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

    public Integer getModifyTimes() {
        return modifyTimes;
    }

    public void setModifyTimes(Integer modifyTimes) {
        this.modifyTimes = modifyTimes;
    }
}