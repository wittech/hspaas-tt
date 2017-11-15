package com.huashi.common.notice.domain;

import java.io.Serializable;
import java.util.Date;

public class EmailSendRecord implements Serializable{
	
	private static final long serialVersionUID = -2910819187953162707L;

	private Integer id;

    private Integer userId;

    private String email;

    private String subject;

    private String content;

    private Date createTime;
    
    public EmailSendRecord() {
		super();
	}

	public EmailSendRecord(Integer userId, String email, String subject, String content, Date createTime) {
		super();
		this.userId = userId;
		this.email = email;
		this.subject = subject;
		this.content = content;
		this.createTime = createTime;
	}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
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

	public EmailSendRecord(String email, String subject, String content) {
		super();
		this.email = email;
		this.subject = subject;
		this.content = content;
	}
    
}