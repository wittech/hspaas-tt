package com.huashi.common.user.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.user.domain.UserBalanceLog;

public interface UserBalanceLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserBalanceLog record);

    int insertSelective(UserBalanceLog record);

    UserBalanceLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBalanceLog record);

    int updateByPrimaryKey(UserBalanceLog record);
    
	/**
	 * boos 分页 后台Boss系统
	 * 
	 * @param params
	 * @return
	 */
	List<UserBalanceLog> findListBoss(Map<String, Object> params);

	/**
	 * boos 分页个数 公用
	 * 
	 * @param params
	 * @return
	 */
	int findCount(Map<String, Object> params);
}