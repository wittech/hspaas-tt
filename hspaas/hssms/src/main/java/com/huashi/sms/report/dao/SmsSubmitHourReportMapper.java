package com.huashi.sms.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.report.domain.SmsSubmitHourReport;

public interface SmsSubmitHourReportMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SmsSubmitHourReport record);

	int insertSelective(SmsSubmitHourReport record);

	SmsSubmitHourReport selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(SmsSubmitHourReport record);

	int updateByPrimaryKey(SmsSubmitHourReport record);

	/**
	 * 
	 * TODO 批量插入信息
	 * 
	 * @param list
	 * @return
	 */
	int batchInsert(List<SmsSubmitHourReport> list);

	/**
	 * 
	 * TODO 查询用户通道短信通道提交统计报告（即用户和通道两个维度）
	 * 
	 * @param userId
	 *            用户ID(为空则查询全部))
	 * @param startDate
	 *            开始时间（毫秒值）
	 * @param endDate
	 *            截止时间（毫秒值）
	 * @return
	 */
	List<SmsSubmitHourReport> selectUserPassageSubmitReport(@Param("userId") Integer userId,
			@Param("startTime") Long startTime,@Param("endTime") Long endTime);

	/**
	 * 
	 * TODO 查询用户短信提交统计报告
	 * 
	 * @param userId
	 *            用户ID(为空则查询全部))
	 * @param startDate
	 *            开始时间（毫秒值）
	 * @param endDate
	 *            截止时间（毫秒值）
	 * @return
	 */
	List<SmsSubmitHourReport> selectUserSubmitReport(@Param("userId") Integer userId,
			@Param("startTime") Long startTime,@Param("endTime") Long endTime);

	/**
	 * 
	 * TODO 获取通道提交统计数据
	 * 
	 * @param passageId
	 *            通道 ID
	 * @param startDate
	 *            开始时间（毫秒值）
	 * @param endDate
	 *            截止时间（毫秒值）
	 * @return
	 */
	List<SmsSubmitHourReport> selectPassageSubmitReport(@Param("passageId") Integer passageId,
			@Param("startTime") Long startTime,@Param("endTime") Long endTime);

	/**
	 * 
	 * TODO 获取省份通道提交统计数据
	 * 
	 * @param passageId
	 *            通道 ID
	 * @param startDate
	 *            开始时间（毫秒值）
	 * @param endDate
	 *            截止时间（毫秒值）
	 * @return
	 */
	List<SmsSubmitHourReport> selectProvinceSubmitReport(@Param("passageId") Integer passageId,
			@Param("startTime") Long startTime,@Param("endTime") Long endTime);
}