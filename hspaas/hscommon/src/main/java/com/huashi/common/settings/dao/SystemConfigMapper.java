package com.huashi.common.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.settings.domain.SystemConfig;

public interface SystemConfigMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SystemConfig record);

	int insertSelective(SystemConfig record);

	SystemConfig selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SystemConfig record);

	int updateByPrimaryKey(SystemConfig record);

	/**
	 * 
	   * TODO 根据类型查询系统配置信息
	   * 
	   * @param type
	   * @return
	 */
	List<SystemConfig> findByType(String type);
	
	/**
	 * 
	   * TODO 根据类型和子类型查询系统配置信息
	   * 
	   * @param type
	   * @return
	 */
	SystemConfig findByTypeAndKey(@Param("type") String type,@Param("key") String key);
}