package com.huashi.mms.task.domain;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.huashi.common.user.model.UserModel;

public class MmsMtTaskPackets {

    private Long      id;

    private Long      sid;

    private String    mobile;

    private Integer   provinceCode;

    /**
     * 省份名称
     */
    private String    provinceName;

    private Integer   cmcp;

    private String    title;

    private String    content;

    private Integer   mobileSize;

    private Long      messageTemplateId;

    private Integer   passageId;

    private String    passageName;

    private Integer   finalPassageId;

    private String    passageProtocol;

    private String    passageUrl;

    private String    passageParameter;

    private String    resultFormat;

    private String    successCode;

    private String    position;

    private Integer   priority;

    private String    forceActions;

    private String    remark;

    private Integer   retryTimes;

    private Integer   status;

    private Date      createTime;

    private Date      updateTime;

    private Integer   userId;
    private String    callback;

    /**
     * 用户自定义内容，用于回填
     */
    private String    attach;

    /**
     * 用户请求报文传入的扩展号码
     */
    private String    extNumber;

    /**
     * 模板扩展号码
     */
    private String    templateExtNumber;

    /**
     * 用户实体属性
     */
    private UserModel userModel;

    /**
     * 通道代码
     */
    private String    passageCode;

    /**
     * 通道流速
     */
    private Integer   passageSpeed;
    
    private String modelId;
    
    private String title;
    
    private String body;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getCmcp() {
        return cmcp;
    }

    public void setCmcp(Integer cmcp) {
        this.cmcp = cmcp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getMobileSize() {
        return mobileSize;
    }

    public void setMobileSize(Integer mobileSize) {
        this.mobileSize = mobileSize;
    }

    public Long getMessageTemplateId() {
        return messageTemplateId;
    }

    public void setMessageTemplateId(Long messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getFinalPassageId() {
        return finalPassageId;
    }

    public void setFinalPassageId(Integer finalPassageId) {
        this.finalPassageId = finalPassageId;
    }

    public String getPassageProtocol() {
        return passageProtocol;
    }

    public void setPassageProtocol(String passageProtocol) {
        this.passageProtocol = passageProtocol == null ? null : passageProtocol.trim();
    }

    public String getPassageUrl() {
        return passageUrl;
    }

    public void setPassageUrl(String passageUrl) {
        this.passageUrl = passageUrl == null ? null : passageUrl.trim();
    }

    public String getPassageParameter() {
        return passageParameter;
    }

    public void setPassageParameter(String passageParameter) {
        this.passageParameter = passageParameter == null ? null : passageParameter.trim();
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat == null ? null : resultFormat.trim();
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode == null ? null : successCode.trim();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getForceActions() {
        return forceActions;
    }

    public void setForceActions(String forceActions) {
        this.forceActions = forceActions == null ? null : forceActions.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber;
    }

    public String getTemplateExtNumber() {
        return templateExtNumber;
    }

    public void setTemplateExtNumber(String templateExtNumber) {
        this.templateExtNumber = templateExtNumber;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getPassageCode() {
        return passageCode;
    }

    public void setPassageCode(String passageCode) {
        this.passageCode = passageCode;
    }

    public Integer getPassageSpeed() {
        return passageSpeed;
    }

    public void setPassageSpeed(Integer passageSpeed) {
        this.passageSpeed = passageSpeed;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getPassageName() {
        return passageName;
    }

    public void setPassageName(String passageName) {
        this.passageName = passageName;
    }

    public char[] getActions() {
        if (StringUtils.isNotBlank(forceActions)) {
            return forceActions.toCharArray();
        }
        return null;
    }
}
