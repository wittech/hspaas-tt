package com.huashi.common.notice.service;

public interface IEmailSendService {

	/**
	 * 
	   * TODO 发送注册校验EMAIL信息
	   * @param email
	   * @return
	 */
	boolean sendRegisterVerifyContent(String email);
}
