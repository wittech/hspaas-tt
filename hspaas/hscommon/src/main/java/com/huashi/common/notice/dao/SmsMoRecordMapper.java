package com.huashi.common.notice.dao;

import com.huashi.common.notice.domain.SmsMoRecord;

public interface SmsMoRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMoRecord record);

    int insertSelective(SmsMoRecord record);

    SmsMoRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMoRecord record);

    int updateByPrimaryKey(SmsMoRecord record);
}