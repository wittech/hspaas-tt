package com.huashi.mms.record.dao;

import java.util.List;
import java.util.Map;

import com.huashi.mms.record.domain.MmsMoMessageReceive;

public interface MmsMoMessageReceiveMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MmsMoMessageReceive record);

    int insertSelective(MmsMoMessageReceive record);

    MmsMoMessageReceive selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMoMessageReceive record);

    int updateByPrimaryKey(MmsMoMessageReceive record);

    List<MmsMoMessageReceive> findPageListByUserId(Map<String, Object> params);

    int getCountByUserId(Map<String, Object> params);

    List<MmsMoMessageReceive> findList(Map<String, Object> params);

    int findCount(Map<String, Object> params);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<MmsMoMessageReceive> list);
}
