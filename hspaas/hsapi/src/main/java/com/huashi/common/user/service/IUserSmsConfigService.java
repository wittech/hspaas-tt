package com.huashi.common.user.service;

import com.huashi.common.user.domain.UserSmsConfig;

/**
 * 
 * TODO 用户短信配置信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月18日 下午6:00:41
 */
public interface IUserSmsConfigService {

	/**
	 * 
	 * TODO 获取用户短信计费字数
	 * 
	 * @param userId
	 * @return
	 */
	UserSmsConfig getByUserId(int userId);
	
	/**
	 * 
	   * TODO 根据用户查询单条计费字数
	   * 
	   * @param userId
	   * @return
	 */
	int getSingleChars(int userId);

	/**
	 * 
	 * TODO 添加短信设置
	 * 
	 * @param userId
	 * @param words
	 * @param extNumber
	 * @return
	 */
	boolean save(int userId, int words, String extNumber);

	/**
	 * 修改
	 * 
	 * @param config
	 * @return
	 */
	boolean update(UserSmsConfig config);

	/**
	 * 
	 * TODO 添加短信设置
	 * 
	 * @param userSmsConfig
	 * @return
	 */
	boolean save(UserSmsConfig userSmsConfig);
	
	/**
	 * 
	   * TODO 重新载入REDIS
	   * @return
	 */
	boolean reloadToRedis();

}
