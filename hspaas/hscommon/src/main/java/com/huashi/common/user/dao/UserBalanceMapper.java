package com.huashi.common.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.user.domain.UserBalance;

public interface UserBalanceMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(UserBalance record);

	int insertSelective(UserBalance record);

	UserBalance selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(UserBalance record);

	int updateByPrimaryKey(UserBalance record);

	/**
	 * 
	 * TODO 根据用户ID查询全部平台余额信息
	 * 
	 * @param userId
	 * @return
	 */
	List<UserBalance> selectByUserId(int userId);

	/**
	 * 根据用户ID和业务类型更新用户余额
	 * 
	 * @param userBalance
	 * @return
	 */
	int updateByUserId(UserBalance userBalance);

	/**
	 * 
	 * TODO 查询业务类型的余额量
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	UserBalance selectByUserIdAndType(@Param("userId") int userId,
			@Param("type") int type);

	/**
	 * boos 分页
	 * 
	 * @param params
	 * @return
	 */
	List<UserBalance> findList(@Param("userId") Integer userId,@Param("start") Integer start, 
			@Param("pageSize") Integer pageSize);

	/**
	 * boos 分页个数
	 * 
	 * @param params
	 * @return
	 */
	int findCount(@Param("userId") Integer userId);

	/**
	 * 
	 * TODO 更新余额告警配置信息
	 * 
	 * @param id
	 * @param mobile
	 * @param threshold
	 * @param remark
	 * @return
	 */
	int updateWarning(UserBalance userBalance);
	
	/**
	 * 
	   * TODO 修改告警状态
	   * @param id
	   * @param status
	   * @return
	 */
	int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
	
	/**
	 * 
	   * TODO 获取有效的用户余额信息（1.用户有效，2.余额告警状态为'正常'）
	   * 
	   * @return
	 */
	List<UserBalance> selectAvaiableUserBalance();
}