package com.huashi.sms.passage.domain;

import java.io.Serializable;
import java.util.Date;

public class SmsPassageControl implements Serializable{
    
	private static final long serialVersionUID = -8122573739791735060L;

	private Integer id;

    private Integer passageId;
    //虚拟列
    private String passageIdText;

    private Integer status;

    private String cron;

    private Integer parameterId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron == null ? null : cron.trim();
    }

    public Integer getParameterId() {
        return parameterId;
    }

    public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getPassageIdText() {
		return passageIdText;
	}

	public void setPassageIdText(String passageIdText) {
		this.passageIdText = passageIdText;
	}
}