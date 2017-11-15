package com.huashi.sms.record.vo;

import java.io.Serializable;

/**
 * 
 * TODO 用户手机号码处理的最后一条短信(1天内)
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月6日 上午1:00:34
 */
public class SmsLastestRecordVo implements Serializable {

	private static final long serialVersionUID = 3102409004246718641L;
	private int userId;
	private String mobile;
	private String content;
	private String createTime;
	private String nodeTime;
	private MessageNode messageNode;
	private String descrption;

	/**
	 * 
	 * TODO 短信节点
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年9月6日 上午12:59:55
	 */
	public enum MessageNode {
		API_CALLING, API_CALL_FAILED, SMS_CREATE, SMS_SUBMITING, SMS_SUBMIT_FAILED,

		SMS_SEND_GATEWAY, SMS_GATEWAY_RECEIVE_FAILED, SMS_COMPLETE
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getNodeTime() {
		return nodeTime;
	}

	public void setNodeTime(String nodeTime) {
		this.nodeTime = nodeTime;
	}

	public MessageNode getMessageNode() {
		return messageNode;
	}

	public void setMessageNode(MessageNode messageNode) {
		this.messageNode = messageNode;
	}

	public String getDescrption() {
		return descrption;
	}

	public void setDescrption(String descrption) {
		this.descrption = descrption;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
