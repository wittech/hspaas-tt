package com.huashi.common.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.user.domain.UserDeveloper;

public interface UserDeveloperMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserDeveloper record);

    int insertSelective(UserDeveloper record);

    UserDeveloper selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDeveloper record);

    int updateByPrimaryKey(UserDeveloper record);
    
    /**
	 * 
	 * TODO 根据用户编号获取接口信息
	 * 
	 * @param userId
	 * @return
	 */
	UserDeveloper selectByUserId(int userId);

	/**
	 * 
	 * TODO 根据接口账号查询开发者信息
	 * 
	 * @param appkey
	 * @return
	 */
	UserDeveloper selectByAppkey(@Param("appkey") String appkey);
	
	/**
	 * 
	 * TODO 根据接口账号查询开发者信息
	 * 
	 * @param appkey
	 * @return
	 */
	UserDeveloper selectByAppkeyAndSecret(@Param("appkey") String appkey, 
			@Param("appsecret") String appSecret);
	
	/**
	 * 
	   * TODO 查询全部
	   * @return
	 */
	List<UserDeveloper> selectAll();
	
}