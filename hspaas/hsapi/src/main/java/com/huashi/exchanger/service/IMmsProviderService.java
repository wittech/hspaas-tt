package com.huashi.exchanger.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.exception.ExchangeProcessException;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.template.domain.MmsMessageTemplate;

/**
 * TODO 彩信服务接口
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月12日 下午6:28:10
 */
public interface IMmsProviderService {

    /**
     * TODO 申请模板
     * 
     * @param mmsMessageTemplate
     * @return
     */
    ProviderModelResponse applyModel(MmsPassageParameter parameter, MmsMessageTemplate mmsMessageTemplate);

    /**
     * TODO 发送彩信至网关
     * 
     * @param parameter 通道参数信息，如果账号，密码，URL等
     * @param mobile 手机号码
     * @param content 短信内容
     * @param extNumber 拓展号码
     * @return
     * @throws ExchangeProcessException
     */
    List<ProviderSendResponse> sendMms(MmsPassageParameter parameter, String mobile, String content, String extNumber)
                                                                                                                      throws ExchangeProcessException;

    /**
     * TODO 下行状态报告（推送）
     * 
     * @param access
     * @param params
     * @return
     */
    List<MmsMtMessageDeliver> doStatusReport(MmsPassageAccess access, JSONObject params);

    /**
     * TODO 下行状态报告（自取）
     * 
     * @param access
     * @return
     */
    List<MmsMtMessageDeliver> pullStatusReport(MmsPassageAccess access);

    /**
     * TODO 上行短信内容（推送）
     * 
     * @param access
     * @param params
     * @return
     */
    List<MmsMoMessageReceive> doMoReport(MmsPassageAccess access, JSONObject params);

    /**
     * TODO 上行短信内容（自取）
     * 
     * @param access
     * @return
     */
    List<MmsMoMessageReceive> doPullMoReport(MmsPassageAccess access);

}
