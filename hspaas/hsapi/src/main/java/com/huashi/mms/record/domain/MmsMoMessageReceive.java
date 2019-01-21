package com.huashi.mms.record.domain;

import java.util.Date;

import com.huashi.common.user.model.UserModel;

public class MmsMoMessageReceive {

    private Long             id;

    private Integer          userId;

    private Integer          passageId;

    private String           msgId;

    private String           mobile;

    private String           content;

    private String           destnationNo;

    private Boolean          needPush;

    private String           pushUrl;

    private String           receiveTime;

    private Date             createTime;

    private Long             createUnixtime;

    private String           passageName;

    private UserModel        userModel;

    private MmsMoMessagePush messagePush;

    private String           sid;
    private Integer          retryTimes;

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

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getDestnationNo() {
        return destnationNo;
    }

    public void setDestnationNo(String destnationNo) {
        this.destnationNo = destnationNo == null ? null : destnationNo.trim();
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

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime == null ? null : receiveTime.trim();
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

    public String getPassageName() {
        return passageName;
    }

    public void setPassageName(String passageName) {
        this.passageName = passageName;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public MmsMoMessagePush getMessagePush() {
        return messagePush;
    }

    public void setMessagePush(MmsMoMessagePush messagePush) {
        this.messagePush = messagePush;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }
}
