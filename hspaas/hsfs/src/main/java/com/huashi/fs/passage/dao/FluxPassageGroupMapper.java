package com.huashi.fs.passage.dao;

import com.huashi.fs.passage.domain.FluxPassageGroup;

public interface FluxPassageGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FluxPassageGroup record);

    int insertSelective(FluxPassageGroup record);

    FluxPassageGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FluxPassageGroup record);

    int updateByPrimaryKey(FluxPassageGroup record);
}