package com.huashi.bill.bill.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.bill.bill.domain.ConsumptionReport;

public interface ConsumptionReportMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(ConsumptionReport record);

	int insertSelective(ConsumptionReport record);

	ConsumptionReport selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(ConsumptionReport record);

	int updateByPrimaryKey(ConsumptionReport record);

	/**
	 * 
	 * TODO 根据用户ID和平台类型获取消费信息
	 * 
	 * @param userId
	 * @param platformType
	 * @return
	 */
	List<ConsumptionReport> selectByUserIdAndType(@Param("userId") int userId, 
			@Param("platformType") int platformType, @Param("limitSize") int limitSize);
	
	/**
	 * 
	   * TODO 批量插入
	   * @param list
	   * @return
	 */
	int batchInsert(@Param("list") List<ConsumptionReport> list);
	
}