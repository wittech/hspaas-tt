package com.huashi.exchanger.resolver.mms.http;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;

/**
 * TODO http 自定义处理器
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月25日 下午4:06:16
 */
@Component
public class MmsHttpSender {

    /**
     * TODO 调用模板报备接口
     * 
     * @param parameter
     * @param mmsMessageTemplate
     * @return
     */
    public ProviderModelResponse applyModel(MmsPassageParameter parameter, MmsMessageTemplate mmsMessageTemplate) {
        return AbstractMmsPassageResolver.getInstance(RequestTemplateHandler.parse(parameter.getParams()).customPassage()).applyModel(parameter,
                                                                                                                                      mmsMessageTemplate);
    }

    /**
     * TODO 调用彩信发送接口
     * 
     * @param parameter
     * @param extNumber
     * @param title
     * @param bobies
     * @return
     */
    public List<ProviderSendResponse> send(MmsPassageParameter parameter, String mobile, String extNumber,
                                           String title, List<MmsMessageTemplateBody> bobies) {
        return AbstractMmsPassageResolver.getInstance(RequestTemplateHandler.parse(parameter.getParams()).customPassage()).send(parameter,
                                                                                                                                mobile,
                                                                                                                                extNumber,
                                                                                                                                title,
                                                                                                                                bobies);
    }

    /**
     * TODO 调用彩信发送接口
     * 
     * @param parameter
     * @param extNumber
     * @param modelId
     * @return
     */
    public List<ProviderSendResponse> send(MmsPassageParameter parameter, String mobile, String extNumber,
                                           String modelId) {
        return AbstractMmsPassageResolver.getInstance(RequestTemplateHandler.parse(parameter.getParams()).customPassage()).send(parameter,
                                                                                                                                mobile,
                                                                                                                                extNumber,
                                                                                                                                modelId);

    }

    /**
     * TODO 解析回执数据报告（推送）
     *
     * @param access
     * @param report
     * @return
     */
    public List<MmsMtMessageDeliver> deliver(MmsPassageAccess access, JSONObject report) {
        TParameter tparameter = RequestTemplateHandler.parse(access.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return customStatusTranslate(report, tparameter, access.getSuccessCode());
        }

        throw new RuntimeException("unkonwn custom code");
    }

    /**
     * TODO 解析回执数据报告（轮训）
     *
     * @param access
     * @return
     */
    public List<MmsMtMessageDeliver> deliver(MmsPassageAccess access) {

        TParameter tparameter = RequestTemplateHandler.parse(access.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return customStatusTranslate(access, tparameter);
        }

        return null;
    }

    /**
     * TODO 用自定义通道（轮训回执解析）
     *
     * @param access
     * @param tparameter
     * @return
     */
    private List<MmsMtMessageDeliver> customStatusTranslate(MmsPassageAccess access, TParameter tparameter) {
        return AbstractMmsPassageResolver.getInstance(tparameter.customPassage()).mtDeliver(tparameter,
                                                                                            access.getUrl(),
                                                                                            access.getSuccessCode());
    }

    /**
     * TODO 调用自定义通道（推送回执解析）
     *
     * @param report
     * @param tparameter
     * @param successCode
     * @return
     */
    private List<MmsMtMessageDeliver> customStatusTranslate(JSONObject report, TParameter tparameter, String successCode) {
        return AbstractMmsPassageResolver.getInstance(tparameter.customPassage()).mtDeliver(report.getString(ParameterFilterContext.PARAMETER_NAME_IN_STREAM),
                                                                                            successCode);
    }

    /**
     * TODO 调用自定义通道(轮训)
     *
     * @param access
     * @param tparameter
     * @return
     */
    private List<MmsMoMessageReceive> customMoTranslate(MmsPassageAccess access, TParameter tparameter) {
        return AbstractMmsPassageResolver.getInstance(tparameter.customPassage()).moReceive(tparameter,
                                                                                            access.getUrl(),
                                                                                            access.getPassageId());
    }

    /**
     * TODO 调用自定义通道（推送解析）
     *
     * @param report
     * @param tparameter
     * @param passageId
     * @return
     */
    private List<MmsMoMessageReceive> customMoTranslate(JSONObject report, TParameter tparameter, Integer passageId) {
        return AbstractMmsPassageResolver.getInstance(tparameter.customPassage()).moReceive(report.getString(ParameterFilterContext.PARAMETER_NAME_IN_STREAM),
                                                                                            passageId);
    }

    /**
     * TODO 解析上行数据报告
     *
     * @param access
     * @param report
     * @return
     */
    public List<MmsMoMessageReceive> mo(MmsPassageAccess access, JSONObject report) {
        TParameter tparameter = RequestTemplateHandler.parse(access.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return customMoTranslate(report, tparameter, access.getPassageId());
        }

        throw new RuntimeException("未配置公共上行解析器");
    }

    /**
     * TODO 解析上行数据报告
     *
     * @param access
     * @return
     */
    public List<MmsMoMessageReceive> mo(MmsPassageAccess access) {
        TParameter tparameter = RequestTemplateHandler.parse(access.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return customMoTranslate(access, tparameter);
        }

        return null;
    }

}
