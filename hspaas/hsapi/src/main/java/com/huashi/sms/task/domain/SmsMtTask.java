package com.huashi.sms.task.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.huashi.common.user.model.UserModel;
import com.huashi.sms.settings.domain.ForbiddenWords;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsActionPosition;
import com.huashi.sms.task.context.TaskContext.TaskSubmitType;

public class SmsMtTask implements Serializable {

    private static final long       serialVersionUID   = -3844143801379748603L;

    private Long                    id;

    private Integer                 userId;

    private Long                    sid;

    private Integer                 appType;

    private String                  mobile;

    private String                  content;

    private String                  extNumber;

    private String                  attach;

    private String                  callback;

    private Integer                 fee;

    private Integer                 totalFee;

    private String                  submitUrl;

    private String                  ip;

    private Date                    createTime;

    private Long                    createUnixtime;

    private Integer                 processStatus;

    private Integer                 approveStatus;

    private String                  errorMobiles;

    private String                  repeatMobiles;

    private String                  finalContent;

    private String                  remark;

    private Date                    processTime;

    private UserModel               userModel;

    private Long                    messageTemplateId;

    private String                  forceActions;

    // 黑名单手机号码（暂存）
    private String                  blackMobiles;

    // 敏感词
    private String                  forbiddenWords;

    // 提交类型：Enum@TaskSubmitType
    private Integer                 submitType         = TaskSubmitType.BATCH_MESSAGE.getCode();

    // 返还条数
    private Integer                 returnFee;

    // 用户原提交手机号码，未进行黑名单等处理的号码
    private String                  originMobile;

    // 点对点短信用户原内容
    private String                  p2pBody;
    // 点对点短信分析后报文内容
    private List<JSONObject>        p2pBodies;

    /**
     * 模板变量值
     */
    private String                  variables;

    // 敏感词标签
    private List<ForbiddenWords>    forbiddenWordLabels;

    private List<SmsMtTaskPackets>  packets;

    // 汇总错误信息
    private transient StringBuilder errorMessageReport = new StringBuilder();
    // 允许操作的强制动作，如敏感词报备，模板报备，切换通道
    private transient StringBuilder forceActionsReport = new StringBuilder("000");

    public char[] getActions() {
        if (StringUtils.isNotBlank(forceActions)) {
            return forceActions.toCharArray();
        }
        return null;
    }

    /**
     * TODO 是否模板存在问题
     * 
     * @return
     */
    public boolean isTemplateError() {
        char[] actions = getActions();
        if (actions != null && actions.length > 2) {
            return actions[PacketsActionPosition.SMS_TEMPLATE_MISSED.getPosition()] == PacketsActionActor.BROKEN.getActor();
        }
        return false;
    }

    /**
     * TODO 是否包含敏感词
     * 
     * @return
     */
    public boolean isWordError() {
        char[] actions = getActions();
        if (actions != null && actions.length > 2) {
            return actions[PacketsActionPosition.FOBIDDEN_WORDS.getPosition()] == PacketsActionActor.BROKEN.getActor();
        }
        return false;
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

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl == null ? null : submitUrl.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
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

    public String getErrorMobiles() {
        return errorMobiles;
    }

    public void setErrorMobiles(String errorMobiles) {
        this.errorMobiles = errorMobiles == null ? null : errorMobiles.trim();
    }

    public String getFinalContent() {
        return finalContent;
    }

    public void setFinalContent(String finalContent) {
        this.finalContent = finalContent == null ? null : finalContent.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public List<SmsMtTaskPackets> getPackets() {
        return packets;
    }

    public void setPackets(List<SmsMtTaskPackets> packets) {
        this.packets = packets;
    }

    public String[] getMobiles() {
        if (StringUtils.isNotBlank(mobile)) {
            return mobile.split(",");
        }
        return null;
    }

    public String getFirstMobile() {
        String[] mobiles = getMobiles();
        if (mobiles != null && mobiles.length > 0) {
            return mobiles[0];
        }
        return mobile;
    }

    public String[] getShowErrorMobiles() {
        if (StringUtils.isNotBlank(errorMobiles)) {
            return errorMobiles.split(",");
        }
        return null;
    }

    public String getShowErrorFirstMobile() {
        String[] mobiles = getShowErrorMobiles();
        if (mobiles != null && mobiles.length > 0) {
            return mobiles[0];
        }
        return mobile;
    }

    public String[] getShowRepeatMobiles() {
        if (StringUtils.isNotBlank(repeatMobiles)) {
            return repeatMobiles.split(",");
        }
        return null;
    }

    public String getShowRepeatFirstMobiles() {
        String[] mobiles = getShowRepeatMobiles();
        if (mobiles != null && mobiles.length > 0) {
            return mobiles[0];
        }
        return mobile;
    }

    public String getOriginMobile() {
        return originMobile;
    }

    public void setOriginMobile(String originMobile) {
        this.originMobile = originMobile;
    }

    public String getRepeatMobiles() {
        return repeatMobiles;
    }

    public void setRepeatMobiles(String repeatMobiles) {
        this.repeatMobiles = repeatMobiles;
    }

    public Integer getReturnFee() {
        return returnFee;
    }

    public void setReturnFee(Integer returnFee) {
        this.returnFee = returnFee;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Long getMessageTemplateId() {
        return messageTemplateId;
    }

    public void setMessageTemplateId(Long messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public String getForceActions() {
        return forceActions;
    }

    public void setForceActions(String forceActions) {
        this.forceActions = forceActions;
    }

    public Integer getSubmitType() {
        return submitType;
    }

    public void setSubmitType(Integer submitType) {
        this.submitType = submitType;
    }

    public List<JSONObject> getP2pBodies() {
        return p2pBodies;
    }

    public void setP2pBodies(List<JSONObject> p2pBodies) {
        this.p2pBodies = p2pBodies;
    }

    public void setP2pBody(String p2pBody) {
        this.p2pBody = p2pBody;
    }

    public String getP2pBody() {
        return p2pBody;
    }

    public String getBlackMobiles() {
        return blackMobiles;
    }

    public void setBlackMobiles(String blackMobiles) {
        this.blackMobiles = blackMobiles;
    }

    public StringBuilder getErrorMessageReport() {
        return errorMessageReport;
    }

    public void setErrorMessageReport(StringBuilder errorMessageReport) {
        this.errorMessageReport = errorMessageReport;
    }

    public StringBuilder getForceActionsReport() {
        return forceActionsReport;
    }

    public void setForceActionsReport(StringBuilder forceActionsReport) {
        this.forceActionsReport = forceActionsReport;
    }

    public String getForbiddenWords() {
        return forbiddenWords;
    }

    public void setForbiddenWords(String forbiddenWords) {
        this.forbiddenWords = forbiddenWords;
    }

    public List<ForbiddenWords> getForbiddenWordLabels() {
        return forbiddenWordLabels;
    }

    public void setForbiddenWordLabels(List<ForbiddenWords> forbiddenWordLabels) {
        this.forbiddenWordLabels = forbiddenWordLabels;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

}
