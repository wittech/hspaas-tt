package com.huashi.sms.record.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.record.domain.SmsMtMessagePush;

public interface SmsMtMessagePushMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SmsMtMessagePush record);

	int insertSelective(SmsMtMessagePush record);

	SmsMtMessagePush selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(SmsMtMessagePush record);

	int updateByPrimaryKey(SmsMtMessagePush record);

	/**
	 * 
	 * TODO 根据手机号码和消息ID查询推送记录信息
	 * 
	 * @param mobile
	 * @param msgId
	 * @return
	 */
	SmsMtMessagePush findByMobileAndMsgid(@Param("mobile") String mobile,
			@Param("msgId") String msgId);

	/**
	 * 
	 * TODO 批量插入信息
	 * 
	 * @param list
	 * @return
	 */
	int batchInsert(List<SmsMtMessagePush> list);
}