package com.huashi.sms.record.dao;

import com.huashi.sms.record.domain.SmsMtMessageDeliverLog;

public interface SmsMtMessageDeliverLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMtMessageDeliverLog record);

    int insertSelective(SmsMtMessageDeliverLog record);

    SmsMtMessageDeliverLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMtMessageDeliverLog record);

    int updateByPrimaryKeyWithBLOBs(SmsMtMessageDeliverLog record);

    int updateByPrimaryKey(SmsMtMessageDeliverLog record);
}