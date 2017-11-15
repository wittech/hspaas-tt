package com.huashi.sms.passage.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.passage.domain.SmsPassageMessageTemplate;

public interface SmsPassageMessageTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsPassageMessageTemplate record);

    int insertSelective(SmsPassageMessageTemplate record);

    SmsPassageMessageTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsPassageMessageTemplate record);

    int updateByPrimaryKey(SmsPassageMessageTemplate record);
    
    /**
     * 
       * TODO 根据通道ID查询通道短信模板信息
       * 
       * @param passageId
       * @return
     */
    List<SmsPassageMessageTemplate> selectByPassageId(@Param("passageId") Integer passageId);
    
    /**
     * 
       * TODO 查询通道短信模板集合信息（用于分页）
       * 
       * @param params
       * @return
     */
    List<SmsPassageMessageTemplate> selectList(Map<String,Object> params);

    /**
     * 
       * TODO查询通道短信模板数量
       * 
       * @param params
       * @return
     */
    int selectCount(Map<String,Object> params);
    
    /**
     * 
       * TODO 根据通道方模板ID查询通道短信模板信息
       * 
       * @param passageId
       * @return
     */
    SmsPassageMessageTemplate selectByPassageTemplateId(@Param("templateId") String templateId);
    
    /**
     * 
       * TODO 查询全部可以通道模板信息
       * 
       * @return
     */
    List<SmsPassageMessageTemplate> selectAvaiable();
}