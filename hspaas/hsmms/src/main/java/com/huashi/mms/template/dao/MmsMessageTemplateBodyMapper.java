package com.huashi.mms.template.dao;

import com.huashi.mms.template.domain.MmsMessageTemplateBody;

public interface MmsMessageTemplateBodyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MmsMessageTemplateBody record);

    int insertSelective(MmsMessageTemplateBody record);

    MmsMessageTemplateBody selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMessageTemplateBody record);

    int updateByPrimaryKey(MmsMessageTemplateBody record);
}