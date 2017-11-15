package com.huashi.common.user.service;

import com.huashi.common.user.exception.LoginException;
import com.huashi.common.vo.SessionUser;

/**
 * 
 * TODO 系统登陆
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年1月9日 下午11:33:59
 */
public interface ILoginService {

	/**
	 * 
	   * TODO 用户登录
	   * @param username
	   * @param password
	   * @return
	   * @throws LoginException
	 */
	SessionUser login(String username, String password) throws LoginException;
}
