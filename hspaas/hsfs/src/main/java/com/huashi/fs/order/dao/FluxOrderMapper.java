package com.huashi.fs.order.dao;

import java.util.List;
import java.util.Map;

import com.huashi.fs.order.domain.FluxOrder;

public interface FluxOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FluxOrder record);

    int insertSelective(FluxOrder record);

    FluxOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FluxOrder record);

    int updateByPrimaryKey(FluxOrder record);
    
    /**
	 * 
	 * TODO 获取总条数
	 * 
	 * @param params
	 * @return
	 */
	int getCountByUserId(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<FluxOrder> findPageListByUserId(Map<String, Object> params);
	
	/**
	 * 根据用户id 和手机号码查询最新数据记录
	 * @param params
	 * @return
	 */
	List<FluxOrder> findUserIdAndMobileRecord(Map<String, Object> params);
}