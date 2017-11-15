package com.huashi.sms.record.domain;

import java.util.Date;

public class SmsMtMessageDeliverLog {
    private Long id;

    private String passageCode;

    private String msgId;

    private String deliverTime;

    private Date createTime;

    private String report;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassageCode() {
        return passageCode;
    }

    public void setPassageCode(String passageCode) {
        this.passageCode = passageCode == null ? null : passageCode.trim();
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
    }

    public String getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(String deliverTime) {
        this.deliverTime = deliverTime == null ? null : deliverTime.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report == null ? null : report.trim();
    }
}