package com.huashi.common.notice.domain;

import java.io.Serializable;
import java.util.Date;

public class SmsMoRecord implements Serializable{
	private static final long serialVersionUID = -7656075331978891402L;

	private Integer id;

    private Integer userId;

    private String mobile;

    private String content;

    private String destnationId;

    private Date createTime;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getDestnationId() {
        return destnationId;
    }

    public void setDestnationId(String destnationId) {
        this.destnationId = destnationId == null ? null : destnationId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}