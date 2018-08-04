package com.huashi.exchanger.service;

import com.huashi.sms.passage.domain.SmsPassageParameter;

public interface ISmsProxyManageService {

    /**
     * TODO 加载CMPP/SMGP 代理
     * 
     * @param parameter
     * @return
     */
    boolean startProxy(SmsPassageParameter parameter);

    /**
     * TODO 根据通道ID判断代理是否可用
     * 
     * @param passageId
     * @return
     */
    boolean isProxyAvaiable(Integer passageId);

    /**
     * TODO 停用代理
     * 
     * @param passageId
     * @return
     */
    boolean stopProxy(Integer passageId);

    /**
     * TODO 获取直连协议对象
     * 
     * @param passageId
     * @return
     */
    Object getManageProxy(Integer passageId);

    /**
     * TODO 累加发送错误次数
     * 
     * @param passageId
     */
    void plusSendErrorTimes(Integer passageId);

    /**
     * TODO 清空通道发送错误次数
     * 
     * @param passageId
     */
    void clearSendErrorTimes(Integer passageId);

}
