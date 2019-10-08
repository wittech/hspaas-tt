package com.huashi.sms.passage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.passage.domain.SmsPassageParameter;

public interface SmsPassageParameterMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SmsPassageParameter record);

    int insertSelective(SmsPassageParameter record);

    SmsPassageParameter selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPassageParameter record);

    int updateByPrimaryKey(SmsPassageParameter record);

    int deleteByPassageId(@Param("passageId") int passageId);

    /**
     * TODO 根据通道ID查询通道所有模板信息
     * 
     * @param passageId
     * @return
     */
    List<SmsPassageParameter> findByPassageId(@Param("passageId") int passageId);

    SmsPassageParameter getByPassageIdAndType(@Param("passageId") int passageId, @Param("callType") int callType);

    /**
     * TODO 根据通道代码和调用类型获取通道参数信息
     * 
     * @param callType
     * @param url
     * @return
     */
    SmsPassageParameter getByTypeAndUrl(@Param("callType") int callType, @Param("url") String url);

    /**
     * TODO 根据通道ID获取 发送协议类型
     * 
     * @param passageId
     * @return
     */
    SmsPassageParameter selectSendProtocol(@Param("passageId") int passageId);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<SmsPassageParameter> list);
}
