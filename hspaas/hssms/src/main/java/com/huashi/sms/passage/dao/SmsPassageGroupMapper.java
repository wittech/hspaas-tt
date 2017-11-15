package com.huashi.sms.passage.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.passage.domain.SmsPassageGroup;

public interface SmsPassageGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsPassageGroup record);

    int insertSelective(SmsPassageGroup record);

    SmsPassageGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPassageGroup record);

    int updateByPrimaryKey(SmsPassageGroup record);
    
    List<SmsPassageGroup> findList(Map<String, Object> params);
    
    int findCount(Map<String, Object> params);
    
    List<SmsPassageGroup> findAll();
    
}