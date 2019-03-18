package com.huashi.mms.template.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.template.domain.MmsMessageTemplateBody;

public interface MmsMessageTemplateBodyMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MmsMessageTemplateBody record);

    int insertSelective(MmsMessageTemplateBody record);

    MmsMessageTemplateBody selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMessageTemplateBody record);

    int updateByPrimaryKey(MmsMessageTemplateBody record);

    List<MmsMessageTemplateBody> selectByTemplateId(@Param("templateId") Long templateId);

    int batchInsert(List<MmsMessageTemplateBody> bodies);

    int deleteByTemplateId(@Param("templateId") Long templateId);

}
