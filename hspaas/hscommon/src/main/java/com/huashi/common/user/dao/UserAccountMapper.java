package com.huashi.common.user.dao;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.user.domain.UserAccount;

public interface UserAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKey(UserAccount record);
    
    UserAccount selectByUserId(@Param("userId") int userId);
}