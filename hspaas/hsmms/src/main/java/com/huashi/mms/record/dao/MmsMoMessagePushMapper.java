package com.huashi.mms.record.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.record.domain.MmsMoMessagePush;

public interface MmsMoMessagePushMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MmsMoMessagePush record);

    int insertSelective(MmsMoMessagePush record);

    MmsMoMessagePush selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMoMessagePush record);

    int updateByPrimaryKey(MmsMoMessagePush record);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<MmsMoMessagePush> list);

    /**
     * 根据手机号码、消息id查询 上行推送信息
     * 
     * @param mobile
     * @param msgId
     * @return
     */
    MmsMoMessagePush findByMobileAndMsgid(@Param("mobile") String mobile, @Param("msgId") String msgId);
}
