package com.huashi.sms.passage.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.passage.domain.SmsPassageControl;

public interface SmsPassageControlMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsPassageControl record);

    int insertSelective(SmsPassageControl record);

    SmsPassageControl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPassageControl record);

    int updateByPrimaryKey(SmsPassageControl record);
    
    /**
     * 查询全部短信通道控制
     * @param params
     * @return
     */
    List<SmsPassageControl> findList(Map<String, Object> params);

    /**
     * 查询短信通道控制总数 （分页）
     * @param params
     * @return
     */
	int count(Map<String, Object> params);
	
	/**
	 * 根据通道id获取控制信息
	 * @param passageId
	 * @return
	 */
    SmsPassageControl selectByPassageId(Integer passageId);
}