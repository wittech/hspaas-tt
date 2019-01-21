package com.huashi.common.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.user.domain.UserMmsConfig;

public interface UserMmsConfigMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserMmsConfig record);

    int insertSelective(UserMmsConfig record);

    UserMmsConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserMmsConfig record);

    int updateByPrimaryKey(UserMmsConfig record);

    /**
     * TODO 根据用户ID查询彩信配置信息
     * 
     * @param userId
     * @return
     */
    UserMmsConfig selectByUserId(@Param("userId") int userId);

    /**
     * TODO 查询所有有效的彩信配置信息
     * 
     * @return
     */
    List<UserMmsConfig> selectAll();
}
