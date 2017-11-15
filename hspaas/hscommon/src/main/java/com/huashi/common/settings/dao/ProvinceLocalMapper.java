package com.huashi.common.settings.dao;

import java.util.List;

import com.huashi.common.settings.domain.ProvinceLocal;

public interface ProvinceLocalMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ProvinceLocal record);

	int insertSelective(ProvinceLocal record);

	ProvinceLocal selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ProvinceLocal record);

	int updateByPrimaryKey(ProvinceLocal record);

	/**
	 * 
	 * TODO 查找可用的手机号码省份归属地信息
	 * 
	 * @return
	 */
	List<ProvinceLocal> selectAvaiable();
}