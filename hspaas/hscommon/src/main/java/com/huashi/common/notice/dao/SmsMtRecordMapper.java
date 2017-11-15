package com.huashi.common.notice.dao;

import com.huashi.common.notice.domain.SmsMtRecord;

public interface SmsMtRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMtRecord record);

    int insertSelective(SmsMtRecord record);

    SmsMtRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMtRecord record);

    int updateByPrimaryKey(SmsMtRecord record);
}