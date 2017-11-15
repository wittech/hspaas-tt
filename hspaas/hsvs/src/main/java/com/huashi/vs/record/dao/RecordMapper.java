package com.huashi.vs.record.dao;

import java.util.List;
import java.util.Map;

import com.huashi.vs.record.domain.Record;

public interface RecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);
    
	/**
	 * 
	 * TODO 总条数
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
	List<Record> findPageListByUserId(Map<String, Object> params);
	
	/**
	 * 根据手机号码 当前用户id 获取记录信息
	 * @param params
	 * @return
	 */
	List<Record> findMobileRecord(Map<String,Object> params);
}