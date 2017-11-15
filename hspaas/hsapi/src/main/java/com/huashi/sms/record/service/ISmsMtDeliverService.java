package com.huashi.sms.record.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public interface ISmsMtDeliverService {

	/**
     * 
       * TODO 根据
       * @param mobile
       * @param msgId
       * @return
     */
    SmsMtMessageDeliver findByMobileAndMsgid(String mobile, String msgId);
    
    /**
     * 
       * TODO 批量插入信息
       * 
       * @param list
       * @return
     */
    int batchInsert(List<SmsMtMessageDeliver> list);
    
    /**
     * 
       * TODO 完成回执逻辑
       * 
       * @param list
       * @return
     */
    int doFinishDeliver(List<SmsMtMessageDeliver> list);
    
    /**
     * 
       * TODO 上家回执数据正常回执 但处理发生异常情况需要记录错误信息，以便人工补偿机制
       * 
       * @param obj
       * @return
     */
    boolean doDeliverToException(JSONObject obj);
    
    /**
     * 
       * TODO 保存网关回执日志数据（统一大报告）
       * @param report
       * @return
     */
    boolean saveDeliverLog(JSONObject report);
    
}
