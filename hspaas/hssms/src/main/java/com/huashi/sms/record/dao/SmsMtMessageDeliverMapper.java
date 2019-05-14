package com.huashi.sms.record.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public interface SmsMtMessageDeliverMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SmsMtMessageDeliver record);

    int insertSelective(SmsMtMessageDeliver record);

    SmsMtMessageDeliver selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMtMessageDeliver record);

    int updateByPrimaryKey(SmsMtMessageDeliver record);

    /**
     * TODO 根据
     * 
     * @param mobile
     * @param msgId
     * @return
     */
    SmsMtMessageDeliver selectByMobileAndMsgid(@Param("msgId") String msgId, @Param("mobile") String mobile);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<SmsMtMessageDeliver> list);

    /**
     * 根据ID集合查询数据
     * 
     * @param list
     * @return
     */
    List<SmsMtMessageDeliver> findByIds(List<String> list);
}
