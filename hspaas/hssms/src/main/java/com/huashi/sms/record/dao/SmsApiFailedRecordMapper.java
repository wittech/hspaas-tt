package com.huashi.sms.record.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.record.domain.SmsApiFailedRecord;

public interface SmsApiFailedRecordMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SmsApiFailedRecord record);

	int insertSelective(SmsApiFailedRecord record);

	SmsApiFailedRecord selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(SmsApiFailedRecord record);

	int updateByPrimaryKey(SmsApiFailedRecord record);

	int selectCount(Map<String, Object> params);

	List<SmsApiFailedRecord> selectPageList(Map<String, Object> params);

	List<SmsApiFailedRecord> findList(Map<String,Object> params);

	int findCount(Map<String,Object> params);
}