package com.huashi.mms.template.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huashi.common.user.model.UserModel;

public class MmsMessageTemplate {

    private Long                               id;

    private Long                               modelId;

    private Integer                            userId;

    private Integer                            status;

    private Byte                               isRunning;

    private Integer                            appType;

    private Integer                            noticeMode;

    private String                             mobile;

    private Integer                            submitInterval;

    private Integer                            limitTimes;

    private Integer                            routeType;

    private Integer                            priority;

    private String                             extNumber;

    private Date                               createTime;

    private Date                               updateTime;

    private Date                               approveTime;

    private String                             approveUser;

    private String                             approveDesc;

    private String                             remark;

    /**
     * 用户信息
     */
    private UserModel                          userModel;

    /**
     * 路由类型名称
     */
    private String                             routeTypeText;

    /**
     * 平台类型名称
     */
    private String                             apptypeText;

    /**
     * 模板报文数据集合
     */
    private final List<MmsMessageTemplateBody> bodies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Byte getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(Byte isRunning) {
        this.isRunning = isRunning;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getNoticeMode() {
        return noticeMode;
    }

    public void setNoticeMode(Integer noticeMode) {
        this.noticeMode = noticeMode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Integer getSubmitInterval() {
        return submitInterval;
    }

    public void setSubmitInterval(Integer submitInterval) {
        this.submitInterval = submitInterval;
    }

    public Integer getLimitTimes() {
        return limitTimes;
    }

    public void setLimitTimes(Integer limitTimes) {
        this.limitTimes = limitTimes;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber == null ? null : extNumber.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser == null ? null : approveUser.trim();
    }

    public String getApproveDesc() {
        return approveDesc;
    }

    public void setApproveDesc(String approveDesc) {
        this.approveDesc = approveDesc == null ? null : approveDesc.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getRouteTypeText() {
        return routeTypeText;
    }

    public void setRouteTypeText(String routeTypeText) {
        this.routeTypeText = routeTypeText;
    }

    public String getApptypeText() {
        return apptypeText;
    }

    public void setApptypeText(String apptypeText) {
        this.apptypeText = apptypeText;
    }

    public List<MmsMessageTemplateBody> getBodies() {
        return bodies;
    }

}
