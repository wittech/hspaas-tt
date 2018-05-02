package com.huashi.sms.settings.domain;

import java.io.Serializable;
import java.util.Date;

import com.huashi.sms.settings.constant.MobileBlacklistType;

public class SmsMobileBlackList implements Serializable {

    private static final long serialVersionUID = -5967207753862319356L;

    private Integer           id;

    private String            mobile;

    private Integer           type             = MobileBlacklistType.NORMAL.getCode();

    private String            remark;

    private Date              createTime;

    private String            typeText;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

}
