package com.huashi.common.user.domain;

import java.io.Serializable;
import java.util.Date;

public class UserAccount implements Serializable{
    
	private static final long serialVersionUID = -393946738800618796L;

	private Integer id;

    private Integer userId;

    private Double money;

    private Double freezeMoney;

    private Date createTime;

    private Date updateTime;

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

    public Double getFreezeMoney() {
        return freezeMoney;
    }

    public void setFreezeMoney(Double freezeMoney) {
        this.freezeMoney = freezeMoney;
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