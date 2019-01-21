package com.huashi.mms.record.domain;

import java.util.Date;

import com.huashi.common.user.model.UserModel;

public class MmsMtMessageSubmit {

    private Long                id;

    private Integer             userId;

    private Long                sid;

    private String              mobile;

    private Integer             provinceCode;

    private Integer             cmcp;

    private Long                templateId;

    private String              title;

    private String              content;

    private String              attach;

    private Integer             passageId;

    private Boolean             needPush;

    private String              pushUrl;

    private Date                createTime;

    private Long                createUnixtime;

    private String              destnationNo;

    private Integer             status;

    private String              remark;

    private String              msgId;

    private UserModel           userModel;

    private MmsMtMessageDeliver messageDeliver;

    private MmsMtMessagePush    messagePush;

    private String              passageName;

    private String              appKey;

    /**
     * 回调网址
     */
    private String              callback;

    /**
     * 针对提交失败补状态码
     */
    private String              pushErrorCode;

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

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach == null ? null : attach.trim();
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Boolean getNeedPush() {
        return needPush;
    }

    public void setNeedPush(Boolean needPush) {
        this.needPush = needPush;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl == null ? null : pushUrl.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUnixtime() {
        return createUnixtime;
    }

    public void setCreateUnixtime(Long createUnixtime) {
        this.createUnixtime = createUnixtime;
    }

    public String getDestnationNo() {
        return destnationNo;
    }

    public void setDestnationNo(String destnationNo) {
        this.destnationNo = destnationNo == null ? null : destnationNo.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public MmsMtMessageDeliver getMessageDeliver() {
        return messageDeliver;
    }

    public void setMessageDeliver(MmsMtMessageDeliver messageDeliver) {
        this.messageDeliver = messageDeliver;
    }

    public MmsMtMessagePush getMessagePush() {
        return messagePush;
    }

    public void setMessagePush(MmsMtMessagePush messagePush) {
        this.messagePush = messagePush;
    }

    public String getPassageName() {
        return passageName;
    }

    public void setPassageName(String passageName) {
        this.passageName = passageName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getPushErrorCode() {
        return pushErrorCode;
    }

    public void setPushErrorCode(String pushErrorCode) {
        this.pushErrorCode = pushErrorCode;
    }
}
