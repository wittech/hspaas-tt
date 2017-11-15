package com.huashi.common.user.dao;

import com.huashi.common.user.domain.UserAccountLog;

public interface UserAccountLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserAccountLog record);

    int insertSelective(UserAccountLog record);

    UserAccountLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserAccountLog record);

    int updateByPrimaryKey(UserAccountLog record);
}