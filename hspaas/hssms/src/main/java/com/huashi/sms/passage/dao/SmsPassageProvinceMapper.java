package com.huashi.sms.passage.dao;

import com.huashi.sms.passage.domain.SmsPassageProvince;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmsPassageProvinceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsPassageProvince record);

    int insertSelective(SmsPassageProvince record);

    SmsPassageProvince selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsPassageProvince record);

    int updateByPrimaryKey(SmsPassageProvince record);

    int deleteByPassageId(@Param("passageId") Integer passageId);

    List<SmsPassageProvince> getListByPassageId(@Param("passageId") Integer
                                                       passageId);
}