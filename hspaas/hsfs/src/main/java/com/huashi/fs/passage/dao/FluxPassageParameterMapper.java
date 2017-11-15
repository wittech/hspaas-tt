package com.huashi.fs.passage.dao;

import com.huashi.fs.passage.domain.FluxPassageParameter;

public interface FluxPassageParameterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FluxPassageParameter record);

    int insertSelective(FluxPassageParameter record);

    FluxPassageParameter selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FluxPassageParameter record);

    int updateByPrimaryKey(FluxPassageParameter record);
}