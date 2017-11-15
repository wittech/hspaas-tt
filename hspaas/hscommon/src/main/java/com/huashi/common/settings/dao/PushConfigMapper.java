package com.huashi.common.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.settings.domain.PushConfig;

public interface PushConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PushConfig record);

    int insertSelective(PushConfig record);

    PushConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PushConfig record);

    int updateByPrimaryKey(PushConfig record);
    
	/**
	 * 
	 * TODO查询用户推送设置记录  返回对象
	 * 
	 * @param userId
	 * @return
	 */
	List<PushConfig> selectByUserId(int userId);
	
	int updateByUserId(PushConfig pushConfig);
	
	/**
	 * 
	   * TODO 根据用户ID和平台类型查询推送信息
	   * 
	   * @param userId
	   * @param type
	   * @return
	 */
	PushConfig selectByUserIdAndType(@Param("userId") int userId,@Param("type") int type);
	
	/**
	 * 
	   * TODO 查找全部配置信息
	   * 
	   * @return
	 */
	List<PushConfig> selectAll();
}