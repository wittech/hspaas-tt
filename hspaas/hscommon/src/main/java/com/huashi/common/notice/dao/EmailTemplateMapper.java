package com.huashi.common.notice.dao;

import com.huashi.common.notice.domain.EmailTemplate;

public interface EmailTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EmailTemplate record);

    int insertSelective(EmailTemplate record);

    EmailTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EmailTemplate record);

    int updateByPrimaryKey(EmailTemplate record);
    
    /**
     * 
       * TODO 根据代码获取模板信息
       * @param code
       * @return
     */
    EmailTemplate selectByCode(Integer code);
}