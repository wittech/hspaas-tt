package com.huashi.common.provider.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.provider.domain.ProviderApiCode;

public interface ProviderApiCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProviderApiCode record);

    int insertSelective(ProviderApiCode record);

    ProviderApiCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProviderApiCode record);

    int updateByPrimaryKey(ProviderApiCode record);
    
    /**
     * 根据类型和状态码查询记录
     * @param params  key { type 1：短信，2:流量，3：语音 .code 状态码}
     * @return
     */
    List<ProviderApiCode> findTypeAndCodeRecord(Map<String,Object> params);
}