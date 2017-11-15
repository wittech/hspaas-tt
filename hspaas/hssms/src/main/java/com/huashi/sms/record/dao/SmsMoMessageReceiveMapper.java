package com.huashi.sms.record.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.record.domain.SmsMoMessageReceive;

public interface SmsMoMessageReceiveMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SmsMoMessageReceive record);

	int insertSelective(SmsMoMessageReceive record);

	SmsMoMessageReceive selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(SmsMoMessageReceive record);

	int updateByPrimaryKey(SmsMoMessageReceive record);

	List<SmsMoMessageReceive> findPageListByUserId(Map<String, Object> params);

	int getCountByUserId(Map<String, Object> params);

	List<SmsMoMessageReceive> findList(Map<String, Object> params);

	int findCount(Map<String, Object> params);
	
	 /**
     * 
       * TODO 批量插入信息
       * 
       * @param list
       * @return
     */
    int batchInsert(List<SmsMoMessageReceive> list);
}