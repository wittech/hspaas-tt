package com.huashi.exchanger.resolver.mms.http.trioly;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.HttpClientManager;
import com.huashi.exchanger.resolver.mms.http.AbstractMmsPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;

/**
 * TODO 三体彩信通道处理器
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月23日 下午5:53:00
 */
@Component
public class TriolyPassageResolver extends AbstractMmsPassageResolver {

    @Override
    public List<ProviderSendResponse> send(MmsPassageParameter parameter, String mobile, String extNumber,
                                           String modelId) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果s
            String result = HttpClientManager.get(parameter.getUrl(),
                                                  sendRequest(tparameter, modelId, mobile, extNumber));

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("三体彩信服务解析失败", e);
            throw new RuntimeException("三体彩信服务解析失败");
        }
    }

    /**
     * TODO 签名
     * 
     * @param appKey
     * @param appId
     * @param mobile
     * @return
     */
    private String signature(String appKey, String appId, String mobile) {
        return DigestUtils.md5Hex(appKey + appId + mobile);
    }

    /**
     * TODO 鉴权签名
     * 
     * @param appKey
     * @param appId
     * @param name
     * @param title
     * @param context
     * @param timestamp
     * @return
     */
    private String signature(String appKey, String appId, String name, String title, String context, String timestamp) {
        String sign = appKey + appId + name + title + context + timestamp;

        try {
            sign = URLEncoder.encode(sign, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("Signature generate failed", e);
        }

        return DigestUtils.md5Hex(sign);
    }

    /**
     * TODO 发送彩信组装请求信息
     * 
     * @param tparameter
     * @param modelId
     * @param mobile
     * @param extNumber 扩展号
     * @return
     */
    private Map<String, Object> sendRequest(TParameter tparameter, String modelId, String mobile, String extNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("appId", tparameter.getString("appId"));
        params.put("modeId", modelId);
        params.put("mobile", mobile);
        params.put("sign", signature(tparameter.getString("appKey"), tparameter.getString("appId"), mobile));

        return params;
    }

    /**
     * TODO 转换最终模板内容
     * 
     * @param template
     * @return
     */
    private String translateContext(MmsMessageTemplate template) {
        if (template == null) {
            throw new RuntimeException("模板数据为空");
        }

        if (CollectionUtils.isEmpty(template.getBodies())) {
            throw new RuntimeException("模板结构体数据为空");
        }

        List<JSONObject> nodes = new ArrayList<>();
        for (MmsMessageTemplateBody body : template.getBodies()) {
            JSONObject node = new JSONObject();
            node.put("type", body.getMediaType());
            node.put("content", body.getUrl());

            nodes.add(node);
        }

        String context = JSON.toJSONString(nodes);
        logger.info("模板内容数据:" + context);

        return context;
    }

    /**
     * TODO 模板报备请求
     * 
     * @param tparameter
     * @param template
     * @return
     */
    private Map<String, Object> sendModelRequest(TParameter tparameter, MmsMessageTemplate template) {
        Map<String, Object> params = new HashMap<>();
        params.put("appId", tparameter.getString("appId"));
        params.put("name", generateModelName(template.getId()));
        params.put("title", generateModelTitle(template.getId()));
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("context", translateContext(template));

        params.put("sign",
                   signature(tparameter.getString("appKey"), tparameter.getString("appId"),
                             tparameter.getString("name"), tparameter.getString("title"),
                             tparameter.getString("context"), tparameter.getString("timestamp")));

        return params;
    }

    /**
     * TODO 模板返回值解析
     * 
     * @param result
     * @param successCode
     * @return
     */
    private ProviderModelResponse modelResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

        Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
        });

        Object code = m.get("code");
        if (code == null || !successCode.equalsIgnoreCase(code.toString())) {
            logger.error("Code[" + code() + "] sendResponse failed, msg is '" + result + "'");
            return null;
        }

        Object rets = m.get("rets");
        if (rets == null || StringUtils.isEmpty(rets.toString())) {
            return null;
        }

        ProviderModelResponse response = new ProviderModelResponse();

        JSONObject modelIdJsonObject = JSON.parseObject(rets.toString());
        if (MapUtils.isEmpty(modelIdJsonObject)) {
            return null;
        }

        response.setSucceess(true);
        response.setCode(code.toString());
        response.setModelId(modelIdJsonObject.getString("modeId"));

        return response;
    }

    /**
     * TODO 解析发送返回值
     * 
     * @param result
     * @param successCode
     * @return
     */
    private List<ProviderSendResponse> sendResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

        Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
        });

        Object code = m.get("code");
        if (code == null || !successCode.equalsIgnoreCase(code.toString())) {
            logger.error("Code[" + code() + "] sendResponse failed, msg is '" + result + "'");
            return null;
        }

        Object rets = m.get("rets");
        if (rets == null || StringUtils.isEmpty(rets.toString())) {
            return null;
        }

        List<ProviderSendResponse> list = new ArrayList<>();
        List<String> l = JSON.parseArray(rets.toString(), String.class);
        ProviderSendResponse response;
        for (String s : l) {
            Map<String, Object> map = JSON.parseObject(s, new TypeReference<Map<String, Object>>() {
            });
            response = new ProviderSendResponse();
            response.setMobile(map.get("mobile").toString());
            response.setStatusCode(map.get("respCode").toString());
            response.setSid(map.get("sendId").toString());
            response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
                                && successCode.equals(response.getStatusCode()));
            response.setRemark(JSON.toJSONString(map));

            list.add(response);
        }
        return list;
    }

    @Override
    public List<MmsMtMessageDeliver> mtDeliver(String report, String successCode) {
        try {
            logger.info("MMS下行状态报告简码：{} =========={}", code(), report);

            JSONObject jsonobj = JSONObject.parseObject(report);
            String msgId = jsonobj.getString("Msg_Id");
            String mobile = jsonobj.getString("Mobile");
            String status = jsonobj.getString("Status");

            // String destId = jsonobj.getString("Dest_Id");
            // String extInfo = null;
            // if (jsonobj.containsKey("ExtInfo")) {
            // extInfo = jsonobj.getString("ExtInfo");
            // }

            List<MmsMtMessageDeliver> list = new ArrayList<>();

            MmsMtMessageDeliver response = new MmsMtMessageDeliver();
            response.setMsgId(msgId);
            response.setMobile(mobile);
            response.setCmcp(CMCP.local(mobile).getCode());
            response.setStatusCode(status);
            response.setStatus((StringUtils.isNotEmpty(status) && status.equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
            response.setDeliverTime(DateUtil.getNow());
            response.setCreateTime(new Date());
            response.setRemark(report);

            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public List<MmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);

            JSONObject jsonobj = JSONObject.parseObject(report);
            String msgId = jsonobj.getString("Msg_Id");
            String destId = jsonobj.getString("Dest_Id");
            String mobile = jsonobj.getString("Mobile");
            String content = jsonobj.getString("Content");

            List<MmsMoMessageReceive> list = new ArrayList<>();

            MmsMoMessageReceive response = new MmsMoMessageReceive();
            response.setPassageId(passageId);
            response.setMsgId(msgId);
            response.setMobile(mobile);
            response.setContent(content);
            response.setDestnationNo(destId);
            response.setReceiveTime(DateUtil.getNow());
            response.setCreateTime(new Date());
            response.setCreateUnixtime(response.getCreateTime().getTime());
            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "trioly";
    }

    @Override
    public ProviderModelResponse applyModel(MmsPassageParameter parameter, MmsMessageTemplate mmsMessageTemplate) {
        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            String result = HttpClientManager.get(parameter.getUrl(), sendModelRequest(tparameter, mmsMessageTemplate));

            return modelResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("三体彩信模板报备服务解析失败", e);
            throw new RuntimeException("三体彩信模板报备服务解析失败");
        }
    }

}
