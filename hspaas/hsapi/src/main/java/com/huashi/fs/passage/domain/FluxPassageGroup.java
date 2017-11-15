package com.huashi.fs.passage.domain;

import java.io.Serializable;

public class FluxPassageGroup implements Serializable{
	private static final long serialVersionUID = 5679162111742788519L;

	private Integer id;

    private String groupName;

    private String comments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }
}