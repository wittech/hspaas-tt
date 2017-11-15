package com.huashi.sms.record.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.record.domain.SmsMoMessagePush;

public interface SmsMoMessagePushMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMoMessagePush record);

    int insertSelective(SmsMoMessagePush record);

    SmsMoMessagePush selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMoMessagePush record);

    int updateByPrimaryKey(SmsMoMessagePush record);
    
    /**
	 * 
	 * TODO 批量插入信息
	 * 
	 * @param list
	 * @return
	 */
	int batchInsert(List<SmsMoMessagePush> list);
	
	/**
	 * 根据手机号码、消息id查询 上行推送信息
	 * @param mobile
	 * @param msgId
	 * @return
	 */
	SmsMoMessagePush findByMobileAndMsgid(@Param("mobile") String mobile, @Param("msgId")String msgId);
}