package com.huashi.fs.passage.domain;

import java.io.Serializable;

public class FluxPassageGroupDetail implements Serializable{
	private static final long serialVersionUID = 7256062059443478409L;

	private Integer id;

    private Integer groupId;

    private Integer passageId;

    private Integer priority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}