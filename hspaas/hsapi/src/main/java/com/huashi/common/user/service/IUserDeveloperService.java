package com.huashi.common.user.service;

import java.util.List;

import com.huashi.common.user.domain.UserDeveloper;

/**
 * 
 * TODO 开发者服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月22日 下午6:10:31
 */

public interface IUserDeveloperService {

	/**
	 * 
	 * TODO 根据用户ID获取用户接口信息
	 * 
	 * @param userId
	 * @return
	 */
	UserDeveloper getByUserId(int userId);

	/**
	 * 
	 * TODO 根据接口账号获取接口账号
	 * 
	 * @param appkey
	 * @return
	 */
	UserDeveloper getByAppkey(String appkey);

	/**
	 * 
	 * TODO 根据接口账号和接口密码查询开发者信息
	 * 
	 * @param appkey
	 * @param appSecret
	 * @return
	 */
	UserDeveloper getByAppkey(String appkey, String appSecret);
	
	/**
	 * 
	   * TODO 保存开发者信息
	   * 
	   * @param userId
	   * @return
	 */
	UserDeveloper saveWithReturn(int userId);
	
	/**
	 * 
	   * TODO 查询所有的开发者信息
	   * @return
	 */
	List<UserDeveloper> findAll();
	
	/**
	 * 
	   * TODO 保存开发者信息
	   * 
	   * @param userId
	   * @return
	 */
	boolean save(int userId);
	
	/**
	 * 
	   * TODO 修改用户开发者信息
	   * 
	   * @param developer
	   * @return
	 */
	boolean update(UserDeveloper developer);
	
	/**
	 * 
	 * TODO 重载用户关系数据到REDIS
	 * 
	 * @return
	 */
	boolean reloadToRedis();
}
