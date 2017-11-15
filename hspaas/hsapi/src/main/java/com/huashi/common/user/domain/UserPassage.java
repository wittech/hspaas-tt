package com.huashi.common.user.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
  * TODO 用户对应的业务通道
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年9月4日 下午11:14:44
 */
public class UserPassage implements Serializable{
    
	private static final long serialVersionUID = 5739523473146684972L;

	private Integer id;

    private Integer userId;

    private Integer type;

    public UserPassage() {
    }

    public UserPassage(Integer userId, Integer type, Integer passageGroupId) {

        this.userId = userId;
        this.type = type;
        this.passageGroupId = passageGroupId;
    }

    private Integer passageGroupId;

    private Date createTime;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPassageGroupId() {
        return passageGroupId;
    }

    public void setPassageGroupId(Integer passageGroupId) {
        this.passageGroupId = passageGroupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}