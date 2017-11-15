package com.huashi.sms.settings.domain;

import java.io.Serializable;
import java.util.Date;

public class ForbiddenWords implements Serializable {
	private static final long serialVersionUID = 8652648544338513081L;

	private Integer id;

	private String word;

	private String label;
	private Integer level;

	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word == null ? null : word.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public ForbiddenWords() {
		super();
	}

	public ForbiddenWords(String label, String word, Integer level) {
		super();
		this.label = label;
		this.word = word;
		this.level = level;
	}
	
}