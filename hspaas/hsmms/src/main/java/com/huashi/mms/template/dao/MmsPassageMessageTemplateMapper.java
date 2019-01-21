package com.huashi.mms.template.dao;

import com.huashi.mms.template.domain.MmsPassageMessageTemplate;

public interface MmsPassageMessageTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MmsPassageMessageTemplate record);

    int insertSelective(MmsPassageMessageTemplate record);

    MmsPassageMessageTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsPassageMessageTemplate record);

    int updateByPrimaryKey(MmsPassageMessageTemplate record);
}