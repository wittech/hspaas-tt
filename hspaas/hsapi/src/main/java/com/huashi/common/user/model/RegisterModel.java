package com.huashi.common.user.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.huashi.common.finance.domain.InvoiceBalance;
import com.huashi.common.notice.domain.NotificationMessage;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.user.context.UserContext.Source;
import com.huashi.common.user.domain.User;
import com.huashi.common.user.domain.UserAccount;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.domain.UserFluxDiscount;
import com.huashi.common.user.domain.UserPassage;
import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.domain.UserSmsConfig;

/**
 * TODO 用户开户参数信息
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年6月29日 下午11:30:07
 */
public class RegisterModel implements Serializable {

	private static final long serialVersionUID = 5409614827785405814L;
	private Source source; // 用户新增来源
	private boolean isSendEmail = true; // 是否发送注册成功邮件

	/****************** 用户账号相关 *****************************/
	private User user; // 用户账号信息
	private UserProfile userProfile; // 用户基础信息

	/****************** 用户账户金额相关 *****************************/
	private UserAccount userAccount; // 用户账户金额
	private List<UserBalance> userBalances; // 用户业务量余额
	private UserFluxDiscount userFluxDiscount; // 流量折扣信息表
	private UserSmsConfig userSmsConfig; // 短信配置信息表
	private InvoiceBalance invoiceBalance; // 可开具发票金额（初始化0）

	// 用户开户平台通道组信息（初始默认）
	private List<UserPassage> passageList = new ArrayList<UserPassage>();

	/****************** 回调URL初始化相关 *****************************/
	private List<PushConfig> pushConfigs;// 推送记录四条（短信[上行、下行]，流量状态报告，语音状态报告）

	/****************** 回调URL初始化相关 *****************************/
	private NotificationMessage notificationMessage; // 系统消息，开户成功等提示,根据系统模板去取

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<UserBalance> getUserBalances() {
		return userBalances;
	}

	public void setUserBalances(List<UserBalance> userBalances) {
		this.userBalances = userBalances;
	}

	public List<PushConfig> getPushConfigs() {
		return pushConfigs;
	}

	public void setPushConfigs(List<PushConfig> pushConfigs) {
		this.pushConfigs = pushConfigs;
	}

	public UserFluxDiscount getUserFluxDiscount() {
		return userFluxDiscount;
	}

	public void setUserFluxDiscount(UserFluxDiscount userFluxDiscount) {
		this.userFluxDiscount = userFluxDiscount;
	}

	public InvoiceBalance getInvoiceBalance() {
		return invoiceBalance;
	}

	public void setInvoiceBalance(InvoiceBalance invoiceBalance) {
		this.invoiceBalance = invoiceBalance;
	}

	public NotificationMessage getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(NotificationMessage notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public RegisterModel(Source source, User user) {
		super();
		this.source = source;
		this.user = user;
	}

	public boolean isSendEmail() {
		return isSendEmail;
	}

	public void setSendEmail(boolean isSendEmail) {
		this.isSendEmail = isSendEmail;
	}

	public List<UserPassage> getPassageList() {
		return passageList;
	}

	public void setPassageList(List<UserPassage> passageList) {
		this.passageList = passageList;
	}

	public UserSmsConfig getUserSmsConfig() {
		return userSmsConfig;
	}

	public void setUserSmsConfig(UserSmsConfig userSmsConfig) {
		this.userSmsConfig = userSmsConfig;
	}

}
