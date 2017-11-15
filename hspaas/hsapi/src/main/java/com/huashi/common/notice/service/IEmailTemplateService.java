package com.huashi.common.notice.service;

import com.huashi.common.notice.domain.EmailTemplate;

/**
 * 
 * TODO 邮件模板接口
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年1月10日 下午4:07:30
 */
public interface IEmailTemplateService {

	/**
	 * 
	 * TODO 获取注册认证邮件模板内容
	 * 
	 * @param url
	 *            根据用户信息生成唯一URL
	 * @return
	 */
	EmailTemplate getRegisterVerifyContent(String url);

	/**
	 * 
	 * TODO 获取注册/开户成功邮件模板内容
	 * 
	 * @param username
	 * 		用户账号
	 * @param appkey
	 * 		接口账号
	 * @param appsecret
	 * 		接口密码
	 * @return
	 */
	EmailTemplate getRegisterSuceessContent(String username, String appkey, String appsecret);
}
