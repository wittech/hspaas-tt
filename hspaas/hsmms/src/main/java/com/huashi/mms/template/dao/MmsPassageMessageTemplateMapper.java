package com.huashi.mms.template.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.template.domain.MmsPassageMessageTemplate;

public interface MmsPassageMessageTemplateMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MmsPassageMessageTemplate record);

    int insertSelective(MmsPassageMessageTemplate record);

    MmsPassageMessageTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsPassageMessageTemplate record);

    int updateByPrimaryKey(MmsPassageMessageTemplate record);

    /**
     * TODO 根据模板ID查询集合
     * 
     * @param templateId
     * @return
     */
    List<MmsPassageMessageTemplate> selectByMmsTemplateId(@Param("templateId") Long templateId);

    /**
     * TODO 根据模板ID和通道ID查询
     * 
     * @param templateId
     * @param passageId
     * @return
     */
    MmsPassageMessageTemplate selectByMmsTemplateIdAndPassageId(@Param("templateId") Long templateId,
                                                                @Param("passageId") int passageId);

    /**
     * TODO 根据模板CODE和通道ID查询
     * 
     * @param modelId
     * @param passageId
     * @return
     */
    MmsPassageMessageTemplate selectByMmsModelIdAndPassageId(@Param("modelId") String modelId,
                                                             @Param("passageId") int passageId);
}
