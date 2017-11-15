package com.huashi.sms.settings.domain;

import com.huashi.common.user.domain.UserProfile;

import java.io.Serializable;
import java.util.Date;

public class SmsMobileWhiteList implements Serializable{
	private static final long serialVersionUID = -8764434366737117342L;

	private Integer id;

    private String mobile;

    private Integer userId;

    private Date createTime;

    private UserProfile userProfile ;

    public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}