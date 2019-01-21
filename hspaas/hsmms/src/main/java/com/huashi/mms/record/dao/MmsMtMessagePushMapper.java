package com.huashi.mms.record.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.record.domain.MmsMtMessagePush;

public interface MmsMtMessagePushMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MmsMtMessagePush record);

    int insertSelective(MmsMtMessagePush record);

    MmsMtMessagePush selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMtMessagePush record);

    int updateByPrimaryKey(MmsMtMessagePush record);
    
    /**
     * 
     * TODO 根据手机号码和消息ID查询推送记录信息
     * 
     * @param mobile
     * @param msgId
     * @return
     */
    MmsMtMessagePush findByMobileAndMsgid(@Param("mobile") String mobile,
            @Param("msgId") String msgId);

    /**
     * 
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<MmsMtMessagePush> list);
}