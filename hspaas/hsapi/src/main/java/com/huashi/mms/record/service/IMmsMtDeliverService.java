package com.huashi.mms.record.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;

public interface IMmsMtDeliverService {

    /**
     * TODO 根据消息ID和手机号码查询回执信息
     * 
     * @param mobile
     * @param msgId
     * @return
     */
    MmsMtMessageDeliver findByMobileAndMsgid(String mobile, String msgId);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    void batchInsert(List<MmsMtMessageDeliver> list);

    /**
     * TODO 完成回执逻辑
     * 
     * @param list
     * @return
     */
    int doFinishDeliver(List<MmsMtMessageDeliver> list);

    /**
     * TODO 上家回执数据正常回执 但处理发生异常情况需要记录错误信息，以便人工补偿机制
     * 
     * @param obj
     * @return
     */
    boolean doDeliverToException(JSONObject obj);

}
