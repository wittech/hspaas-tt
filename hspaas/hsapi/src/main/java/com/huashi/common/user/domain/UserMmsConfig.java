package com.huashi.common.user.domain;

import java.io.Serializable;
import java.util.Date;

public class UserMmsConfig implements Serializable {

    private static final long serialVersionUID = 5912110917762140084L;

    private Long              id;

    private Integer           userId;

    private Integer           mmsReturnRule;

    private Long              mmsTimeout;

    private Boolean           messagePass;

    private String            extNumber;

    private Integer           submitInterval;

    private Integer           limitTimes;

    private Date              createTime;

    private Date              updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSmsReturnRule() {
        return mmsReturnRule;
    }

    public void setSmsReturnRule(Integer mmsReturnRule) {
        this.mmsReturnRule = mmsReturnRule;
    }

    public Long getSmsTimeout() {
        return mmsTimeout;
    }

    public void setSmsTimeout(Long mmsTimeout) {
        this.mmsTimeout = mmsTimeout;
    }

    public Boolean getMessagePass() {
        return messagePass;
    }

    public void setMessagePass(Boolean messagePass) {
        this.messagePass = messagePass;
    }

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber == null ? null : extNumber.trim();
    }

    public Integer getSubmitInterval() {
        return submitInterval;
    }

    public void setSubmitInterval(Integer submitInterval) {
        this.submitInterval = submitInterval;
    }

    public Integer getLimitTimes() {
        return limitTimes;
    }

    public void setLimitTimes(Integer limitTimes) {
        this.limitTimes = limitTimes;
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
