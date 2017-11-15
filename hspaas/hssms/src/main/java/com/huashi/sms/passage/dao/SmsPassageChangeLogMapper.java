package com.huashi.sms.passage.dao;

import com.huashi.sms.passage.domain.SmsPassageChangeLog;

public interface SmsPassageChangeLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsPassageChangeLog record);

    int insertSelective(SmsPassageChangeLog record);

    SmsPassageChangeLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPassageChangeLog record);

    int updateByPrimaryKey(SmsPassageChangeLog record);
}