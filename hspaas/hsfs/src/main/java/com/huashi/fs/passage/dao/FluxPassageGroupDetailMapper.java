package com.huashi.fs.passage.dao;

import com.huashi.fs.passage.domain.FluxPassageGroupDetail;

public interface FluxPassageGroupDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FluxPassageGroupDetail record);

    int insertSelective(FluxPassageGroupDetail record);

    FluxPassageGroupDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FluxPassageGroupDetail record);

    int updateByPrimaryKey(FluxPassageGroupDetail record);
}