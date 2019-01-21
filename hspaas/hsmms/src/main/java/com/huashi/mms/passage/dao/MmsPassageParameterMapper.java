package com.huashi.mms.passage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.passage.domain.MmsPassageParameter;

public interface MmsPassageParameterMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(MmsPassageParameter record);

    int insertSelective(MmsPassageParameter record);

    MmsPassageParameter selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmsPassageParameter record);

    int updateByPrimaryKey(MmsPassageParameter record);

    int deleteByPassageId(@Param("passageId") int passageId);

    /**
     * TODO 根据通道ID查询通道所有模板信息
     * 
     * @param passageId
     * @return
     */
    List<MmsPassageParameter> findByPassageId(@Param("passageId") int passageId);

    /**
     * TODO 根据通道代码和调用类型获取通道参数信息
     * 
     * @param callType
     * @param url
     * @return
     */
    MmsPassageParameter getByTypeAndUrl(@Param("callType") int callType, @Param("url") String url);

    /**
     * TODO 根据通道ID获取 发送协议类型
     * 
     * @param passageId
     * @return
     */
    MmsPassageParameter selectSendProtocol(@Param("passageId") int passageId);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<MmsPassageParameter> list);
}
