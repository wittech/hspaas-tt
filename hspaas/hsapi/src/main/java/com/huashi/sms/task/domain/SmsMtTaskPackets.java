package com.huashi.sms.task.domain;

import java.beans.Transient;
import java.io.Serializable;

import com.huashi.common.user.model.UserModel;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsActionPosition;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class SmsMtTaskPackets implements Serializable {

	private static final long serialVersionUID = 8642988970825916882L;

	private Long id;

	private Long sid;

	private String mobile;

	private Integer cmcp;

	private Integer provinceCode;

	// 省份名称
	private String provinceName;

	private String content;

	private Integer mobileSize;

	private Long messageTemplateId;

	private Integer passageId;

	private Integer finalPassageId;

	private String passageProtocol;

	private String passageUrl;

	private String passageParameter;

	private String resultFormat;

	private String successCode;

	private String position;

	private Integer priority;

	private String forceActions;

	private String remark;

	private Integer retryTimes;

	private Integer status;

	private Date createTime;

	private Date updateTime;

	private String passageName;

	private Integer userId;
	private String callback;
	// 用户自定义内容
	private String attach;
	
	// 用户传入的扩展号码
	private String extNumber;
	// add by 20170621 短信模板扩展号码
	private String templateExtNumber;
	
	private int fee = 1;
	// 单条计费（主要针对点对点和模板点对点）
	private int singleFee =1;
	private UserModel userModel;

	// 通道代码
	private String passageCode;
	// 通道流速
	private Integer passageSpeed;
	// 通道签名模式
	private Integer passageSignMode;

	public char[] getActions() {
		if (StringUtils.isNotBlank(forceActions)) {
			return forceActions.toCharArray();
		}
		return null;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	/**
	 * 
	 * TODO 是否模板存在问题
	 * 
	 * @return
	 */
	public boolean isTemplateError() {
		char[] actions = getActions();
		if (actions != null && actions.length > 2) {
			return actions[PacketsActionPosition.SMS_TEMPLATE_MISSED.getPosition()] == PacketsActionActor.BROKEN
					.getActor();
		}
		return false;
	}

	/**
	 * 
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

	/**
	 * 
	 * TODO 是否无可用通道
	 * 
	 * @return
	 */
	public boolean isPassageError() {
		char[] actions = getActions();
		if (actions != null && actions.length > 2) {
			return actions[PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition()] == PacketsActionActor.BROKEN
					.getActor();
		}
		return false;
	}

	public String getPassageName() {
		return passageName;
	}

	public void setPassageName(String passageName) {
		this.passageName = passageName;
	}

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public Integer getCmcp() {
		return cmcp;
	}

	public void setCmcp(Integer cmcp) {
		this.cmcp = cmcp;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	@Transient
	public String[] getMobiles() {
		if (StringUtils.isNotBlank(mobile)) {
			return mobile.split(",");
		}
		return null;
	}

	@Transient
	public String getFirstMobile() {
		String[] mobiles = getMobiles();
		if (mobiles != null && mobiles.length > 0) {
			return mobiles[0];
		}
		return mobile;
	}

	public Integer getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(Integer provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getPassageCode() {
		return passageCode;
	}

	public void setPassageCode(String passageCode) {
		this.passageCode = passageCode;
	}

	public int getSingleFee() {
		return singleFee;
	}

	public void setSingleFee(int singleFee) {
		this.singleFee = singleFee;
	}

	public Integer getPassageSpeed() {
		return passageSpeed;
	}

	public void setPassageSpeed(Integer passageSpeed) {
		this.passageSpeed = passageSpeed;
	}

	public Integer getPassageSignMode() {
		return passageSignMode;
	}

	public void setPassageSignMode(Integer passageSignMode) {
		this.passageSignMode = passageSignMode;
	}

	public String getTemplateExtNumber() {
		return templateExtNumber;
	}

	public void setTemplateExtNumber(String templateExtNumber) {
		this.templateExtNumber = templateExtNumber;
	}

}