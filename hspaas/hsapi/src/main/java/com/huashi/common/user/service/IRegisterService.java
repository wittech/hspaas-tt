package com.huashi.common.user.service;

import com.huashi.common.user.model.RegisterModel;

/**
 * 
 * TODO 平台用户注册
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年1月10日 下午2:22:50
 */
public interface IRegisterService {

	/**
	 * 
	 * TODO 发送校验邮箱内容至用户填写邮箱（发送前验证邮箱的有效性）
	 * 
	 * @param email
	 * @return
	 */
	boolean sendEmailBeforeVerify(String email);

	/**
	 * 
	 * TODO 用户注册/系统开户
	 * 
	 * @param RegisterModel
	 * @return
	 */
	boolean register(RegisterModel model);
}
