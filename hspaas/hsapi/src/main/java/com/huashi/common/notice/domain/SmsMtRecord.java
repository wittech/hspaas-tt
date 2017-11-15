package com.huashi.common.notice.domain;

import java.io.Serializable;
import java.util.Date;

public class SmsMtRecord implements Serializable{
	private static final long serialVersionUID = -2716953972314750278L;

	private Integer id;

    private Integer userId;

    private String mobile;

    private String content;

    private Date createTime;

    private Integer status;

    private Date receiveTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

	public SmsMtRecord(Integer userId, String mobile, String content, Date createTime) {
		super();
		this.userId = userId;
		this.mobile = mobile;
		this.content = content;
		this.createTime = createTime;
	}

	public SmsMtRecord() {
		super();
	}
    
}