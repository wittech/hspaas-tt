package com.huashi.common.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.user.domain.UserSmsConfig;

public interface UserSmsConfigMapper {
	int deleteByPrimaryKey(Long id);

	int insert(UserSmsConfig record);

	int insertSelective(UserSmsConfig record);

	UserSmsConfig selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(UserSmsConfig record);

	int updateByPrimaryKey(UserSmsConfig record);

	UserSmsConfig selectByUserId(@Param("userId") int userId);
	
	List<UserSmsConfig> selectAll();
}