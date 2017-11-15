package com.huashi.common.settings.dao;

import java.util.List;

import com.huashi.common.settings.domain.BalanceRemind;

public interface BalanceRemindMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BalanceRemind record);

    int insertSelective(BalanceRemind record);

    BalanceRemind selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BalanceRemind record);

    int updateByPrimaryKey(BalanceRemind record);
    
    /**
	 * 
	 * TODO根据用户id查询集合
	 * 
	 * @param userId
	 * @return
	 */
	List<BalanceRemind> selectByUserId(int userId);
	
	/**
	 * 根据用户id查询对象
	 * @param userId
	 * @return
	 */
	BalanceRemind getByUserId(int userId);
}