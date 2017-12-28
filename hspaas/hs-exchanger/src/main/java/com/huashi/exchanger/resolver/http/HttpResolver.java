package com.huashi.exchanger.resolver.http;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.custom.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.DeliverTemplateHandler;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.handler.ResponseTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * 
  * TODO http 自定义处理器
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年12月28日 上午10:00:17
 */
@Component
public class HttpResolver{

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * TODO 调用上家通道接口
     *
     * @param parameter
     * @param mobile
     * @param content
     * @param extNumber
     * @return
     */
    public List<ProviderSendResponse> post(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {
        TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return sendCustomTranslate(parameter, mobile, content, extNumber, tparameter);
        }

        setNecesaryMessageNode(tparameter, mobile, content);

        // 转换参数，并调用网关接口，接收返回结果
        String result = HttpClientUtil.post(parameter.getUrl(), tparameter);

        logger.info("发送接口返回值：{}", result);

        // 解析返回结果并返回
        return ResponseTemplateHandler.parse(result, parameter.getResultFormat(), parameter.getPosition(),
                parameter.getSuccessCode());
    }

    /**
     * TODO 设置必要的参数设置，如手机号码，短信内容； 注：后期需要考虑扩展号码
     *
     * @param tparameter
     * @param mobile
     * @param content
     */
    private void setNecesaryMessageNode(TParameter tparameter, String mobile, String content) {
        // 设置用户手机号
        if (tparameter.containsKey(TParameter.MOBILE_NODE_NAME)) {
            tparameter.put(tparameter.get(TParameter.MOBILE_NODE_NAME).toString(), mobile);
            tparameter.remove(TParameter.MOBILE_NODE_NAME);
        } else {
            tparameter.put(TParameter.MOBILE_NODE_NAME, mobile);
        }

        // 设置短信内容
        if (tparameter.containsKey(TParameter.CONTENT_NODE_NAME)) {
            tparameter.put(tparameter.get(TParameter.CONTENT_NODE_NAME).toString(), content);
            tparameter.remove(TParameter.CONTENT_NODE_NAME);
        } else {
            tparameter.put(TParameter.CONTENT_NODE_NAME, content);
        }

        // 设置消息ID，如果对方需要我方 设置此 消息ID（用于比对返回状态数据），则需要设置，默认与SID一致
        // PS:大部分情况不存在此数据，由网关自行设置
//		if (tparameter.containsKey(TParameter.MSGID_NODE_NAME)) {
//			tparameter.put(tparameter.get(TParameter.MSGID_NODE_NAME).toString(), model.getSid());
//			tparameter.remove(TParameter.MSGID_NODE_NAME);
//		}

        // 判断用户的拓展号码，需要结合通道是否支持拓展和 拓展的位数综合判断，判断用户传递的此参数是否为空及是否有权限拓展
//		model.getExtNumber();

    }

    /**
     * TODO 调用自定义通道
     *
     * @param parameter  通道模板参数
     * @param mobile     手机号码
     * @param content    短信内容
     * @param extNumber  扩展号码
     * @param tparameter
     * @return
     */
    private List<ProviderSendResponse> sendCustomTranslate(SmsPassageParameter parameter, String mobile,
                                                           String content, String extNumber, TParameter tparameter) {
        
        return AbstractPassageResolver.getInstance(tparameter.customPassage()).send(parameter, mobile, content, extNumber);
    }

    /**
     * TODO 解析回执数据报告（推送）
     *
     * @param access
     * @param report
     * @return
     */
    public List<SmsMtMessageDeliver> deliver(SmsPassageAccess access,
                                             JSONObject report) {

        TParameter tparameter = RequestTemplateHandler.parse(access.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return customStatusTranslate(report, tparameter, access.getSuccessCode());
        }

        return DeliverTemplateHandler.translate(access, report);
    }

    /**
     * TODO 解析回执数据报告（轮训）
     *
     * @param access
     * @return
     */
    public List<SmsMtMessageDeliver> deliver(SmsPassageAccess access) {

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
    private List<SmsMtMessageDeliver> customStatusTranslate(SmsPassageAccess access, TParameter tparameter) {
        return AbstractPassageResolver.getInstance(tparameter.customPassage()).mtPullDeliver(tparameter, access.getUrl(), access.getSuccessCode());
    }

    /**
     * TODO 调用自定义通道（推送回执解析）
     *
     * @param report
     * @param tparameter
     * @param successCode
     * @return
     */
    private List<SmsMtMessageDeliver> customStatusTranslate(JSONObject report, TParameter tparameter, String successCode) {
        return AbstractPassageResolver.getInstance(tparameter.customPassage()).mtDeliver(report.getString(ParameterFilterContext.PARAMETER_NAME_IN_STREAM), successCode);
    }

    /**
     *  TODO 调用自定义通道(轮训)
     *
     * @param access
     * @param tparameter
     * @return
     */
    private List<SmsMoMessageReceive> customMoTranslate(SmsPassageAccess access, TParameter tparameter) {
        return AbstractPassageResolver.getInstance(tparameter.customPassage()).moPullReceive(tparameter, access.getUrl(), access.getPassageId());
    }

    /**
     * TODO 调用自定义通道（推送解析）
     *
     * @param report
     * @param tparameter
     * @param passageId
     * @return
     */
    private List<SmsMoMessageReceive> customMoTranslate(JSONObject report, TParameter tparameter, Integer passageId) {
        return AbstractPassageResolver.getInstance(tparameter.customPassage()).moReceive(report.getString(ParameterFilterContext.PARAMETER_NAME_IN_STREAM), passageId);
    }


    /**
     * TODO 解析上行数据报告
     *
     * @param access
     * @param report
     * @return
     */
    public List<SmsMoMessageReceive> mo(SmsPassageAccess access,
                                        JSONObject report) {
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
    public List<SmsMoMessageReceive> mo(SmsPassageAccess access) {
        TParameter tparameter = RequestTemplateHandler.parse(access.getParams());
        if (StringUtils.isNotEmpty(tparameter.customPassage())) {
            return customMoTranslate(access, tparameter);
        }

        return null;
    }

}
