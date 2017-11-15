package com.huashi.common.notice.dao;

import com.huashi.common.notice.domain.SmsTemplate;

public interface SmsTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsTemplate record);

    int insertSelective(SmsTemplate record);

    SmsTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsTemplate record);

    int updateByPrimaryKey(SmsTemplate record);
    
    /**
     * 
       * TODO 根据编码查询短信模板信息
       * @param code
       * @return
     */
    SmsTemplate selectByCode(int code);
}