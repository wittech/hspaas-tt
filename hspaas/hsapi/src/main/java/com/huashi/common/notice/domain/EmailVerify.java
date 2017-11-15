package com.huashi.common.notice.domain;

import java.io.Serializable;
import java.util.Date;

public class EmailVerify implements Serializable {
    
	private static final long serialVersionUID = -505889529296007649L;

	private Integer id;

    private String uid;

    private String email;

    private String curlInfo;

    private Date sendTime;

    private Date validTime;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getCurlInfo() {
        return curlInfo;
    }

    public void setCurlInfo(String curlInfo) {
        this.curlInfo = curlInfo == null ? null : curlInfo.trim();
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}