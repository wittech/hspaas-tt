package com.huashi.common.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.user.domain.User;
import com.huashi.common.user.model.UserModel;

public interface UserMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(User record);

	int insertSelective(User record);

	User selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKey(User record);

	/**
	 * 
	 * TODO 根据邮箱号码查询用户
	 * 
	 * @param email
	 * @return
	 */
	int selectCountByEmail(String email);

	/**
	 * 
	 * TODO 根据手机号码查询用户
	 * 
	 * @param mobile
	 * @return
	 */
	int selectCountByMobile(String mobile);

	/**
	 * 
	 * TODO 根据用户获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	User getByUsername(String username);

	/**
	 * 
	 * TODO 根据用户名和密码获取用户信息
	 * 
	 * @param params
	 * @return
	 */
	int getByUsernameAndPassword(Map<String, String> params);

	/**
	 * 
	   * TODO 更新用户状态
	   * @param userId
	   * @param flag
	   * 	状态
	   * @return
	 */
	int updateUserState(@Param("userId") int userId, @Param("flag") String flag);

	/**
	 * 
	   * TODO 更新用户信息
	   * 
	   * @param user
	   * @return
	 */
	int updateUserInfo(User user);
	
	/**
	 * 
	   * TODO 获取所有可用用户信息
	   * @return
	 */
	List<User> selectAvaiableUserList();
	
	/**
	 * 
	   * TODO 查询全部用户映射信息
	   * @return
	 */
	List<UserModel> selectAllMapping();
	
	/**
	 * 
	   * TODO 根据用户ID查询用户映射信息
	   * @param userId
	   * @return
	 */
	UserModel selectMappingByUserId(@Param("userId") int userId);

	User getByEmail(@Param("email") String email);

	User getByMobile(@Param("mobile") String mobile);
}