package com.huashi.common.notice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.notice.domain.NotificationMessage;

public interface NotificationMessageMapper {
	int deleteByPrimaryKey(int id);

	int insert(NotificationMessage record);

	int insertSelective(NotificationMessage record);

	NotificationMessage selectByPrimaryKey(int id);

	int updateByPrimaryKeySelective(NotificationMessage record);

	int updateByPrimaryKeyWithBLOBs(NotificationMessage record);

	int updateByPrimaryKey(NotificationMessage record);

	int getCountByUserId(Map<String, Object> params);

	List<NotificationMessage> findPageListByUserId(Map<String, Object> params);

	List<NotificationMessage> findList(Map<String, Object> params);

	int findCount(Map<String, Object> params);

	NotificationMessage findById(int id);

	/**
	 * 
	 * TODO 根据用户ID获取未读私信信息
	 * 
	 * @param userId
	 * @return
	 */
	List<NotificationMessage> findUnReadByUserId(@Param("userId") int userId);
	
	/**
	 * 
	   * TODO 更新未读状态至已读
	   * @param userId
	   * @return
	 */
	int updateToRead(int userId);
}