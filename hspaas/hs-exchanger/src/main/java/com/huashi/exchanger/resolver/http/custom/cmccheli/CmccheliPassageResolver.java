package com.huashi.exchanger.resolver.http.custom.cmccheli;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.HttpClientManager;
import com.huashi.exchanger.resolver.http.custom.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * TODO 江苏能力营销平台能力调用接口处理器
 *
 * @author zhengying
 * @version V1.0
 * @date 2018年1月24日 下午19:56:32
 * @url http://www.cmccheli.com/
 */
@Component
public class CmccheliPassageResolver extends AbstractPassageResolver {

    /**
     * 短信上行是否开启
     */
    private volatile boolean isMoPushRunning = false;

    /**
     * 短信签名前缀符号
     */
    private static final String MESSAGE_SIGNATURE_PRIFIX = "【";

    /**
     * 短信签名后缀符号
     */
    private static final String MESSAGE_SIGNATURE_SUFFIX = "】";

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content, String extNumber) {
        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 鉴权头信息设置
            Map<String, Object> headers = sendRequestHeader(tparameter);

            // 如果上行推送未启动，则第一次需要启动，不需要加锁，幂等
            if (!isMoPushRunning) {
                try {
                    startMoPush(headers, tparameter.getString("mo_subscribe_url"), tparameter.getString("mo_callback_url"), 
                                tparameter.getString("terminal_no"), parameter.getSuccessCode());
                } catch (Exception e) {
                    logger.error("江苏和力云启动推送上行异常", e);
                }
            }

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientManager.postJson(parameter.getUrl(), headers, sendMtRequestParameter(tparameter, mobile, content, extNumber, parameter.getSmsTemplateId(), buildVariableParamsReport(parameter.getVariableParamNames(), parameter.getVariableParamValues())));

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("江苏和力云发送失败", e);
            throw new RuntimeException("江苏和力云发送发送");
        }
    }

    /**
     * TODO 根据短信内容获取签名内容
     *
     * @param content 短信内容
     * @return
     */
    private static String pickupSignature(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }

        return content.substring(content.lastIndexOf(MESSAGE_SIGNATURE_PRIFIX) + 1, content.lastIndexOf(MESSAGE_SIGNATURE_SUFFIX));
    }

    /**
     * TODO 获取短信内容信息（去掉签名信息）
     * @param content
     * @return
     */
    private static String cutSignatureInContent(String content) {
        return content.substring(content.indexOf(MESSAGE_SIGNATURE_SUFFIX) + 1, content.length());
    }

    /**
     * TODO 组装JSON变量参数报文信息
     *
     * @param paramNames  参数名称
     * @param paramValues 参数值
     * @return
     */
    private String buildVariableParamsReport(String[] paramNames, String[] paramValues) {
        try {
            JSONObject paramReport = new JSONObject();
            for (int i = 0; i < paramNames.length; i++) {

                // 如果参数值包含【】 签名的方括号，则表明参数为短信内容，短信内容需要去掉 签名值（通道方会自动携带签名信息）
                if(StringUtils.isNotEmpty(paramValues[i]) && paramValues[i].contains(MESSAGE_SIGNATURE_PRIFIX) &&
                        paramValues[i].contains(MESSAGE_SIGNATURE_SUFFIX)) {
                    paramReport.put(paramNames[i], cutSignatureInContent(paramValues[i]));
                } else {
                    paramReport.put(paramNames[i], paramValues[i]);
                }
            }
            return paramReport.toJSONString();
        } catch (Exception e) {
            logger.error("江苏和力云组装发送参数信息", e);
            return null;
        }
    }


    /**
     * TODO 发送短信组装请求信息
     *
     * @param tparameter
     * @return
     * @throws NoSuchAlgorithmException
     */
    private Map<String, Object> sendRequestHeader(TParameter tparameter) throws NoSuchAlgorithmException {

        try {
            String apiKey = tparameter.getString("appkey");
            String apiSecret = tparameter.getString("password");
            String time = System.currentTimeMillis() + "";

            Map<String, String> headerParams = new HashMap<>();
            headerParams.put("apiKey", apiKey);
            headerParams.put("time", time);
            headerParams.put("sign", DigestUtils.md5Hex(apiKey + apiSecret + time));

            String authorization = new String(Base64.encodeBase64(JSON.toJSONString(headerParams).getBytes("UTF-8")));
            Map<String, Object> headers = new HashMap<>();

            headers.put("Authorization", authorization);
            headers.put("Content-Type", "text/json; charset=utf-8");

            return headers;

        } catch (Exception e) {
            logger.error("江苏和力云计算签名失败", e);
            return null;
        }
    }


    /**
     * TODO 发送短信组装请求信息
     *
     * @param tparameter
     * @param mobile
     * @param content
     * @param extNumber         扩展号
     * @param passageTemplateId 通道方短信模板ID
     * @param varParams         通道变量信息
     * @return
     */
    private static String sendMtRequestParameter(TParameter tparameter, String mobile, String content, String extNumber, 
                                                 String passageTemplateId, String varParams) {
        JSONObject params = new JSONObject();
        params.put("templateId", passageTemplateId);
        params.put("templateParameter", JSON.parse(varParams));
        params.put("mobiles", mobile.split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR));
        // 1：需要推送状态数据
        params.put("needReceipt", 1);
        // 推送状态URL回调地址
        params.put("receiptNotificationURL", tparameter.get("mt_callback_url"));
        // 短信内容中提取签名信息（不需要携带【】）
        params.put("messageSign", pickupSignature(content));

        //码号
        String terminalNo = tparameter.getString("terminal_no");
        if(StringUtils.isNotBlank(terminalNo))
            params.put("smsCustomCode", terminalNo + (StringUtils.isNotBlank(extNumber) ? extNumber : ""));

        return params.toJSONString();
    }

    /**
     * TODO 解析发送返回值
     *
     * @param result
     * @param successCode
     * @return
     */
    private static List<ProviderSendResponse> sendResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

        JSONObject report = JSON.parseObject(result);

        List<ProviderSendResponse> list = new ArrayList<>();
        ProviderSendResponse response;
        Integer resultCode = report.getInteger("resultCode");

        // 手机号码报文信息， eg: "msgList": [{"mobile": "18867103703","resultCode": 416,"resultMsg": "mobile belong to black list"}]
        List<Map<String, Object>> mobileReport = JSON.parseObject(report.getString("msgList"), new TypeReference<List<Map<String, Object>>>() {
        });
        // 如果返回状态码为错误，直接不做手机号码包解析
        if (!resultCode.toString().equalsIgnoreCase(successCode) || CollectionUtils.isEmpty(mobileReport)) {
            response = new ProviderSendResponse();
            response.setStatusCode(resultCode.toString());
            response.setSid(report.getString("messageId"));
            response.setSuccess(false);
            response.setRemark(report.getString("resultMsg"));

            list.add(response);
            return list;
        }

        // 遍历手机号码信息
        for (Map<String, Object> mr : mobileReport) {
            response = new ProviderSendResponse();
            response.setMobile(mr.get("mobile").toString());
            response.setStatusCode(mr.get("resultCode").toString());
            response.setSid(report.getString("messageId"));
            response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode()) && successCode.equals(response.getStatusCode()));
            response.setRemark(mr.get("resultMsg").toString());
            list.add(response);
        }

        return list;
    }

    /**
     * TODO 下行状态推送报告解析
     *
     * @param report
     * @param successCode
     * @return
     */
    @Override
    public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
        try {
            logger.info("下行状态报告简码：{} =========={}", code(), report);

            JSONObject jsonobj = JSONObject.parseObject(report);
            String msgId = jsonobj.getString("messageId");
            String status = jsonobj.getString("state");
            // 手机号码前两位为86 ，需要截取去掉
            String mobile = StringUtils.isEmpty(jsonobj.getString("destTerminalId")) ? "" : jsonobj.getString("destTerminalId").substring(2);

            String receiveTime = jsonobj.getString("doneTime");

            List<SmsMtMessageDeliver> list = new ArrayList<>();

            SmsMtMessageDeliver response = new SmsMtMessageDeliver();
            response.setMsgId(msgId);
            response.setMobile(mobile);
            response.setStatusCode(status);
            response.setStatus((StringUtils.isNotEmpty(status) && status.equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
            response.setDeliverTime(StringUtils.isEmpty(receiveTime) ? DateUtil.getNow() : receiveTime);
            response.setCreateTime(new Date());
            response.setRemark(report);

            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("江苏和力云状态解析失败", e);
            throw new RuntimeException("江苏和力云状态解析失败");
        }
    }

    @Override
    public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);

            JSONObject jsonobj = JSONObject.parseObject(report);
            String destId = jsonobj.getString("san");
            String mobile = jsonobj.getString("sender");
            String content = jsonobj.getString("message");
            String receiveTime = jsonobj.getString("sendTime");

            List<SmsMoMessageReceive> list = new ArrayList<>();

            SmsMoMessageReceive response = new SmsMoMessageReceive();
            response.setPassageId(passageId);
            response.setMobile(mobile);
            response.setContent(content);
            response.setDestnationNo(destId);
            response.setReceiveTime(StringUtils.isEmpty(receiveTime) ? DateUtil.getNow() : receiveTime);
            response.setCreateTime(new Date());
            response.setCreateUnixtime(response.getCreateTime().getTime());
            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("江苏和力云上行解析失败", e);
            throw new RuntimeException("江苏和力云上行解析失败");
        }
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "cmccheli";
    }

    /**
     * TODO 开启上行推送报告（首次开启，可能并发多次，幂等），暂时考虑同步 @Async
     *
     * @param headers       鉴权头信息
     * @param moUrl         上行URL
     * @param moCallbackUrl 上行推送回调URL
     * @param terminalNo    上行码号，暂时为空
     * @param successCode   成功状态码
     * @return
     */
    private void startMoPush(Map<String, Object> headers, String moUrl, String moCallbackUrl, String terminalNo, String successCode) {
        try {
            String result = HttpClientManager.postJson(moUrl, headers, sendMoRequestParameter(moCallbackUrl));

            JSONObject response = JSON.parseObject(result);
            String resultCode = response.getString("resultCode");
            if (successCode.equalsIgnoreCase(resultCode)) {
                isMoPushRunning = true;
                logger.info("江苏和力云上行推送URL：{} 已开启", moCallbackUrl);
                return;
            }

            logger.warn("江苏和力云上行推送URL：{} 开启失败 , {}", moCallbackUrl, result);

        } catch (Exception e) {
            logger.error("江苏和力云上行推送开启失败， URL: {} ", moUrl, e);
        }
    }

    /**
     * TODO 短信上行开启推送参数信息
     *
     * @param moCallbackUrl
     * @return
     */
    private static String sendMoRequestParameter(String moCallbackUrl) {
        JSONObject params = new JSONObject();
        params.put("smsURL", moCallbackUrl);
//        flg	String	Flg 1:启动，0：终止
        params.put("flg", "1");
        params.put("san", "");

        return params.toJSONString();
    }
}
