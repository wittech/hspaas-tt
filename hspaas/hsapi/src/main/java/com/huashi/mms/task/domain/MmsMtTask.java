package com.huashi.mms.task.domain;

import java.util.Date;
import java.util.List;

import com.huashi.common.user.model.UserModel;

public class MmsMtTask {

    private Long                          id;

    private Integer                       userId;

    private Long                          sid;

    private Integer                          appType;

    private String                        title;

    private String                        content;

    private String                        extNumber;

    private String                        attach;

    private String                        callback;

    private Integer                       fee;

    private Integer                       returnFee;

    private String                        ip;

    private Integer                       processStatus;

    private Integer                       approveStatus;

    private String                        finalContent;

    private Long                          messageTemplateId;

    private Date                          processTime;

    private String                        forceActions;

    private String                        remark;

    private Date                          createTime;

    private Long                          createUnixtime;

    private String                        mobile;

    private String                        errorMobiles;

    private String                        repeatMobiles;

    private String                        blackMobiles;

    /**
     * 用户原提交手机号码
     */
    private String                        originMobile;

    /**
     * 子任务列表
     */
    private List<MmsMtTaskPackets>        packets;

    /**
     * 用户实体属性
     */
    private UserModel                     userModel;

    // 汇总错误信息
    private final transient StringBuilder errorMessageReport = new StringBuilder();
    // 允许操作的强制动作，如敏感词报备，模板报备，切换通道
    private final transient StringBuilder forceActionsReport = new StringBuilder("000");

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getErrorMobiles() {
        return errorMobiles;
    }

    public void setErrorMobiles(String errorMobiles) {
        this.errorMobiles = errorMobiles == null ? null : errorMobiles.trim();
    }

    public String getRepeatMobiles() {
        return repeatMobiles;
    }

    public void setRepeatMobiles(String repeatMobiles) {
        this.repeatMobiles = repeatMobiles == null ? null : repeatMobiles.trim();
    }

    public String getBlackMobiles() {
        return blackMobiles;
    }

    public void setBlackMobiles(String blackMobiles) {
        this.blackMobiles = blackMobiles == null ? null : blackMobiles.trim();
    }

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

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
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

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber == null ? null : extNumber.trim();
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach == null ? null : attach.trim();
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback == null ? null : callback.trim();
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Integer getReturnFee() {
        return returnFee;
    }

    public void setReturnFee(Integer returnFee) {
        this.returnFee = returnFee;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(Integer processStatus) {
        this.processStatus = processStatus;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getFinalContent() {
        return finalContent;
    }

    public void setFinalContent(String finalContent) {
        this.finalContent = finalContent == null ? null : finalContent.trim();
    }

    public Long getMessageTemplateId() {
        return messageTemplateId;
    }

    public void setMessageTemplateId(Long messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
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

    public String getOriginMobile() {
        return originMobile;
    }

    public void setOriginMobile(String originMobile) {
        this.originMobile = originMobile;
    }

    public List<MmsMtTaskPackets> getPackets() {
        return packets;
    }

    public void setPackets(List<MmsMtTaskPackets> packets) {
        this.packets = packets;
    }

    public StringBuilder getErrorMessageReport() {
        return errorMessageReport;
    }

    public StringBuilder getForceActionsReport() {
        return forceActionsReport;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
