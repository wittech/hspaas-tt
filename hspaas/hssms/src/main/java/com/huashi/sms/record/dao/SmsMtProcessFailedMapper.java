package com.huashi.sms.record.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.record.domain.SmsMtProcessFailed;

public interface SmsMtProcessFailedMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMtProcessFailed record);

    int insertSelective(SmsMtProcessFailed record);

    SmsMtProcessFailed selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMtProcessFailed record);

    int updateByPrimaryKey(SmsMtProcessFailed record);
    
    List<SmsMtProcessFailed> findList(Map<String,Object> params);

    int findCount(Map<String,Object> params);

    List<SmsMtProcessFailed> getBySid(long sid);
}