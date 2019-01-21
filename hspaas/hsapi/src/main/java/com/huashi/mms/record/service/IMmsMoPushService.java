package com.huashi.mms.record.service;

import java.util.List;

import com.huashi.mms.record.domain.MmsMoMessagePush;

/**
 * TODO 彩信上行推送服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 上午11:05:26
 */
public interface IMmsMoPushService {

    /**
     * TODO 保存推送记录
     * 
     * @param list
     */
    int savePushMessage(List<MmsMoMessagePush> pushes);

}
