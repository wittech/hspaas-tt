package com.huashi.sms.task.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.task.domain.SmsMtTaskPackets;

public interface SmsMtTaskPacketsMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SmsMtTaskPackets record);

	int insertSelective(SmsMtTaskPackets record);

	SmsMtTaskPackets selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(SmsMtTaskPackets record);

	int updateByPrimaryKey(SmsMtTaskPackets record);

	/**
	 * 
	   * TODO 根据SID查询所有子任务信息
	   * @param sid
	   * @return
	 */
	List<SmsMtTaskPackets> selectBySid(@Param("sid") long sid);

	/**
	 * 
	   * TODO 根据SID更新短信内容信息
	   * @param sid
	   * @param content
	   * @return
	 */
	int updateContent(@Param("sid") long sid, @Param("content") String content);

	/**
	 * 
	 * TODO 批量插入信息
	 * 
	 * @param list
	 * @return
	 */
	int batchInsert(List<SmsMtTaskPackets> list);

	/**
	 * 
	   * TODO 根据SID更新子任务状态
	   * @param sid
	   * @param status
	   * @return
	 */
	int updateStatusBySid(@Param("sid") long sid, @Param("status") int status);

	/**
	 * 
	   * TODO 根据ID更新子任务状态
	   * @param id
	   * @param status
	   * @return
	 */
	int updateStatusById(@Param("id") long id, @Param("status") int status);

	/**
	 * 
	   * TODO 根据子任务ID集合批量更新状态
	   * @param id
	   * @param status
	   * @return
	 */
	int updateStatusByMultiIds(@Param("ids") List<Integer> ids, @Param("status") int status);

	/**
	 * 
	 * TODO 删除子任务
	 * 
	 * @param sid
	 * @return
	 */
	int deleteBySid(@Param("sid") Long sid);

	/**
	 * 
	 * TODO 查询子任务通道错误数量
	 * 
	 * @param sid
	 * @return
	 */
	int selectPassageErrorCount(@Param("sid") Long sid);
	
	
	/**
	 * 
	   * TODO 根据SID查询待处理的子任务数量
	   * @param sid
	   * @return
	 */
	int selectWaitingCount(@Param("sid") Long sid);
}