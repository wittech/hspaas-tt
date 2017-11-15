package com.huashi.common.passage.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.passage.domain.PassageTemplate;

public interface PassageTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PassageTemplate record);

    int insertSelective(PassageTemplate record);

    PassageTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PassageTemplate record);

    int updateByPrimaryKey(PassageTemplate record);
    
    List<PassageTemplate> findList(Map<String, Object> params);
    
    int findCount(Map<String, Object> params);
    
    List<PassageTemplate> findByPassageType(int type);
}