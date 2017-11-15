package com.huashi.sms.record.domain;

import java.io.Serializable;
import java.util.Date;

public class SmsMoMessagePush implements Serializable{
    
	private static final long serialVersionUID = 2668633789030695868L;

	private Long id;

    private String msgId;

    private String mobile;

    private String content;

    private Integer status;

    private Integer retryTimes;

    private Long responseMilliseconds;

    private String responseContent;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Long getResponseMilliseconds() {
        return responseMilliseconds;
    }

    public void setResponseMilliseconds(Long responseMilliseconds) {
        this.responseMilliseconds = responseMilliseconds;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent == null ? null : responseContent.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}