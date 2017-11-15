package com.huashi.sms.passage.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SmsPassageGroup implements Serializable{
	private static final long serialVersionUID = -1212466834787576477L;

	private Integer id;

    private String passageGroupName;

    private String comments;
    
    private List<SmsPassageGroupDetail> detailList = new ArrayList<SmsPassageGroupDetail>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassageGroupName() {
        return passageGroupName;
    }

    public void setPassageGroupName(String passageGroupName) {
        this.passageGroupName = passageGroupName == null ? null : passageGroupName.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

	public List<SmsPassageGroupDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<SmsPassageGroupDetail> detailList) {
		this.detailList = detailList;
	}

    
    
}