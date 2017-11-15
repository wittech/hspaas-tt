package com.huashi.sms.passage.dao;

import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;

import java.util.List;
import java.util.Map;

public interface SmsPassageReachrateSettingsMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SmsPassageReachrateSettings record);

    int insertSelective(SmsPassageReachrateSettings record);

    SmsPassageReachrateSettings selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPassageReachrateSettings record);

    int updateByPrimaryKey(SmsPassageReachrateSettings record);

    List<Long> findAllPassageId();

    List<SmsPassageReachrateSettings> findList(Map<String,Object> params);

    Integer findCount(Map<String,Object> params);

    List<SmsPassage> getPassageByUseable();

    List<SmsPassageReachrateSettings> getByUseable();


}