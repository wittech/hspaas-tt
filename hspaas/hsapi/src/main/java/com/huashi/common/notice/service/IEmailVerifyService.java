package com.huashi.common.notice.service;

public interface IEmailVerifyService {

	/**
	 * 
	   * TODO 添加邮件验证信息
	   * @param url
	   * 		激活URL
	   * @param uid
	   * 		激活码
	   * @param email
	   * 		电子邮箱
	 */
	boolean saveEmailVerify(String url, String uid, String email);
	
	/**	
	 * 
	   * TODO 验证激活码是否有效
	   * @param uid
	   * 		激活码
	   * @return
	   * @throws Exception
	 */
	String isAvaiable(String uid) throws Exception;
	
	/**
	 * 
	   * TODO 激活邮箱后，作废uid
	   * @param uid
	 */
	void activeEmail(String uid);
}
