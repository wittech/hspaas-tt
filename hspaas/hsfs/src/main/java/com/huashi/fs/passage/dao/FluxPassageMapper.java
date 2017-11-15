package com.huashi.fs.passage.dao;

import com.huashi.fs.passage.domain.FluxPassage;

public interface FluxPassageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FluxPassage record);

    int insertSelective(FluxPassage record);

    FluxPassage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FluxPassage record);

    int updateByPrimaryKey(FluxPassage record);
}