package com.huashi.exchanger.resolver.mms.http;

import java.util.List;

import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.template.domain.MmsMessageTemplate;

public interface MmsHttpPassageResolver {

    /**
     * TODO 申请模板
     * 
     * @param parameter 模板参数
     * @param mmsMessageTemplate 模板详细信息
     * @return
     */
    ProviderModelResponse applyModel(MmsPassageParameter parameter, MmsMessageTemplate mmsMessageTemplate);

    /**
     * TODO 发送彩信（提交至通道商）
     *
     * @param parameter 通道参数
     * @param mobile 手机号码
     * @param content 短信内容
     * @param extNumber 用户扩展号码
     * @return
     */
    List<ProviderSendResponse> send(MmsPassageParameter parameter, String mobile, String content, String extNumber);

    /**
     * TODO 下行状态报告回执(推送)
     *
     * @param report
     * @return
     */
    List<MmsMtMessageDeliver> mtDeliver(String report, String successCode);

    /**
     * TODO 下行状态报告回执（自取）
     *
     * @param tparameter
     * @param url
     * @param successCode
     * @return
     */
    List<MmsMtMessageDeliver> mtDeliver(TParameter tparameter, String url, String successCode);

    /**
     * TODO 上行短信状态回执
     *
     * @param report
     * @return
     */
    List<MmsMoMessageReceive> moReceive(String report, Integer passageId);

    /**
     * TODO 上行短信状态回执
     *
     * @param tparameter
     * @param url
     * @param passageId
     * @return
     */
    List<MmsMoMessageReceive> moReceive(TParameter tparameter, String url, Integer passageId);

    /**
     * TODO 用户余额查询
     *
     * @param param
     * @return
     */
    Double balance(TParameter tparameter, String url, Integer passageId);
}
