package com.huashi.common.user.domain;

import java.io.Serializable;
import java.util.Date;

public class UserSmsConfig implements Serializable {

    private static final long serialVersionUID = 3278785600007657647L;

    private Long              id;

    private Integer           userId;

    private Integer           smsWords;

    private Integer           smsReturnRule;

    private Long              smsTimeout;

    /**
     * 是否需要审核
     */
    private Boolean           messagePass      = true;

    private Boolean           needTemplate     = true;

    /**
     * 自动提取模板，1期暂不做
     */
    private Boolean           autoTemplate;

    private Integer           signatureSource;

    private String            signatureContent;

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

    public Integer getSmsWords() {
        return smsWords;
    }

    public void setSmsWords(Integer smsWords) {
        this.smsWords = smsWords;
    }

    public Integer getSmsReturnRule() {
        return smsReturnRule;
    }

    public void setSmsReturnRule(Integer smsReturnRule) {
        this.smsReturnRule = smsReturnRule;
    }

    public Long getSmsTimeout() {
        return smsTimeout;
    }

    public void setSmsTimeout(Long smsTimeout) {
        this.smsTimeout = smsTimeout;
    }

    public Boolean getMessagePass() {
        return messagePass;
    }

    public void setMessagePass(Boolean messagePass) {
        this.messagePass = messagePass;
    }

    public Boolean getNeedTemplate() {
        return needTemplate;
    }

    public void setNeedTemplate(Boolean needTemplate) {
        this.needTemplate = needTemplate;
    }

    public Boolean getAutoTemplate() {
        return autoTemplate;
    }

    public void setAutoTemplate(Boolean autoTemplate) {
        this.autoTemplate = autoTemplate;
    }

    public Integer getSignatureSource() {
        return signatureSource;
    }

    public void setSignatureSource(Integer signatureSource) {
        this.signatureSource = signatureSource;
    }

    public String getSignatureContent() {
        return signatureContent;
    }

    public void setSignatureContent(String signatureContent) {
        this.signatureContent = signatureContent == null ? null : signatureContent.trim();
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

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber;
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

}
