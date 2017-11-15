package com.huashi.common.passage.dao;

import java.util.List;

import com.huashi.common.passage.domain.PassageTemplateDetail;

public interface PassageTemplateDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PassageTemplateDetail record);

    int insertSelective(PassageTemplateDetail record);

    PassageTemplateDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PassageTemplateDetail record);

    int updateByPrimaryKey(PassageTemplateDetail record);
    
    List<PassageTemplateDetail> findListByTemplateId(int templateId);
    
    int deleteByTemplateId(int templateId);
}