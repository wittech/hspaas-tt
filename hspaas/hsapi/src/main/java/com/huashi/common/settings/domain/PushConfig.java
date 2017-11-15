package com.huashi.common.settings.domain;

import java.io.Serializable;
import java.util.Date;

public class PushConfig implements Serializable {

	private static final long serialVersionUID = 8671166859132256455L;

	private Integer id;
	private int userId;
	private String url;
	private Integer type;
	private Integer retryTimes;
	private Integer status;
	private String remark;
	private Date createTime;
	private Date modifyTime;

	public PushConfig() {
	}

	public PushConfig(int userId, String url, Integer type) {
		this.userId = userId;
		this.url = url;
		this.type = type;
	}

	public PushConfig(int userId, String url, Integer type,Integer status) {
		this.userId = userId;
		this.url = url;
		this.type = type;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}