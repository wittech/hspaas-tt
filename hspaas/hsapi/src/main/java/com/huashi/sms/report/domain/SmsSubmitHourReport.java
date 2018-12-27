package com.huashi.sms.report.domain;

import java.io.Serializable;

import com.huashi.common.user.model.UserModel;

public class SmsSubmitHourReport implements Serializable {

    private static final long serialVersionUID = -4052169158389147731L;

    private Long              id;

    private Integer           userId;

    // 用户相关信息
    private UserModel         userModel;

    private Integer           passageId;
    // 通道名称
    private String            passageName;

    private Integer           provinceCode;
    // 省份名称
    private String            pronvinceName;

    private Integer           submitCount;

    private Integer           billCount;

    private Integer           unknownCount;

    private Integer           successCount;

    private Integer           submitFailedCount;

    private Integer           otherCount;

    private Integer           status;

    private Integer           bornHours;

    private Long              hourTime;

    private String            statDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Integer submitCount) {
        this.submitCount = submitCount;
    }

    public Integer getBillCount() {
        return billCount;
    }

    public void setBillCount(Integer billCount) {
        this.billCount = billCount;
    }

    public Integer getUnknownCount() {
        return unknownCount;
    }

    public void setUnknownCount(Integer unknownCount) {
        this.unknownCount = unknownCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getSubmitFailedCount() {
        return submitFailedCount;
    }

    public void setSubmitFailedCount(Integer submitFailedCount) {
        this.submitFailedCount = submitFailedCount;
    }

    public Integer getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(Integer otherCount) {
        this.otherCount = otherCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBornHours() {
        return bornHours;
    }

    public void setBornHours(Integer bornHours) {
        this.bornHours = bornHours;
    }

    public Long getHourTime() {
        return hourTime;
    }

    public void setHourTime(Long hourTime) {
        this.hourTime = hourTime;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getPassageName() {
        return passageName;
    }

    public void setPassageName(String passageName) {
        this.passageName = passageName;
    }

    public String getPronvinceName() {
        return pronvinceName;
    }

    public void setPronvinceName(String pronvinceName) {
        this.pronvinceName = pronvinceName;
    }

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

}
