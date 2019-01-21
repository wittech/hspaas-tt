package com.huashi.sms.passage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.passage.domain.SmsPassageProvince;

public interface SmsPassageProvinceMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SmsPassageProvince record);

    int insertSelective(SmsPassageProvince record);

    SmsPassageProvince selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsPassageProvince record);

    int updateByPrimaryKey(SmsPassageProvince record);

    int deleteByPassageId(@Param("passageId") Integer passageId);

    /**
     * TODO 根据通道ID获取省份通道信息
     * 
     * @param passageId
     * @return
     */
    List<SmsPassageProvince> getListByPassageId(@Param("passageId") Integer passageId);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<SmsPassageProvince> list);
}
