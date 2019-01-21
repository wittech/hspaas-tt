package com.huashi.mms.passage.dao;

import java.util.List;
import java.util.Map;

import com.huashi.mms.passage.domain.MmsPassageGroup;

public interface MmsPassageGroupMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(MmsPassageGroup record);

    int insertSelective(MmsPassageGroup record);

    MmsPassageGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmsPassageGroup record);

    int updateByPrimaryKey(MmsPassageGroup record);

    List<MmsPassageGroup> findList(Map<String, Object> params);

    int findCount(Map<String, Object> params);

    List<MmsPassageGroup> findAll();
}
