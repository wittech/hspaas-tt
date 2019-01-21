package com.huashi.exchanger.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.exception.ExchangeProcessException;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public interface ISmsProviderService {

    /**
     * TODO 发送短信至网关
     * 
     * @param parameter 通道参数信息，如果账号，密码，URL等
     * @param mobile 手机号码
     * @param content 短信内容
     * @param extNumber 短信计费条数
     * @param extNumber 拓展号码
     * @return
     * @throws ExchangeProcessException
     */
    List<ProviderSendResponse> sendSms(SmsPassageParameter parameter, String mobile, String content, Integer fee,
                                       String extNumber) throws ExchangeProcessException;

    /**
     * TODO 下行状态报告（推送）
     * 
     * @param access
     * @param report
     * @return
     */
    List<SmsMtMessageDeliver> receiveMtReport(SmsPassageAccess access, JSONObject report);

    /**
     * TODO 下行状态报告（自取）
     * 
     * @param access
     * @return
     */
    List<SmsMtMessageDeliver> pullMtReport(SmsPassageAccess access);

    /**
     * TODO 上行短信内容（推送）
     * 
     * @param access
     * @param params
     * @return
     */
    List<SmsMoMessageReceive> receiveMoReport(SmsPassageAccess access, JSONObject params);

    /**
     * TODO 上行短信内容（自取）
     * 
     * @param access
     * @return
     */
    List<SmsMoMessageReceive> pullMoReport(SmsPassageAccess access);

}
