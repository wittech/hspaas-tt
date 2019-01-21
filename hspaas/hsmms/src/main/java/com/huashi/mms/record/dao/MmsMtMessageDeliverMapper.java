package com.huashi.mms.record.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.record.domain.MmsMtMessageDeliver;

public interface MmsMtMessageDeliverMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MmsMtMessageDeliver record);

    int insertSelective(MmsMtMessageDeliver record);

    MmsMtMessageDeliver selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMtMessageDeliver record);

    int updateByPrimaryKey(MmsMtMessageDeliver record);
    
    /**
     * 
       * TODO 根据
       * @param mobile
       * @param msgId
       * @return
     */
    MmsMtMessageDeliver selectByMobileAndMsgid(@Param("msgId") String msgId, @Param("mobile") String mobile);
    
    /**
     * 
       * TODO 批量插入信息
       * 
       * @param list
       * @return
     */
    int batchInsert(List<MmsMtMessageDeliver> list);
}