package com.huashi.exchanger.resolver.sms.http.lvcheng;

import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.HttpClientManager;
import com.huashi.exchanger.resolver.sms.http.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * 绿城短信处理器
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2019年06月17日 下午10:17:54
 */
@Component
public class LvchengPassageResolver extends AbstractPassageResolver {

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientManager.post(parameter.getUrl(),
                                                   sendRequest(tparameter, mobile, content, extNumber));

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("绿城发送解析失败 mobile: {}, content:{}, parameter : {}", mobile, content,
                         JSON.toJSONString(parameter), e);
            throw new RuntimeException("解析失败");
        }
    }

    /**
     * 发送短信组装请求信息
     * 
     * @param tparameter 参数信息
     * @param mobile 手机号码
     * @param content 短信内容
     * @param extNumber 扩展号
     * @return 参数报文
     */
    private static Map<String, Object> sendRequest(TParameter tparameter, String mobile, String content,
                                                   String extNumber) {
        String timestamps = System.currentTimeMillis() + "";

        Map<String, Object> params = new HashMap<>();
        params.put("name", tparameter.getString("account"));
        params.put("pswd", tparameter.getString("password"));
        params.put("mobile", mobile);
        params.put("msg", content);
        params.put("needstatus", "true");
        params.put("timestamp", timestamps);
        params.put("sender", extNumber == null ? "" : extNumber);

        // 可选参数。定时发送时间。yyyyMMddHHmm 格式
        // params.put("attime", "yyyyMMddHHmm");

        return params;
    }

    /**
     * 解析发送返回值
     * 
     * @param result 返回结果
     * @param successCode 成功码
     * @return 数据吹结果
     */
    private static List<ProviderSendResponse> sendResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject == null) {
            return null;
        }

        List<ProviderSendResponse> list = new ArrayList<>();
        ProviderSendResponse response = new ProviderSendResponse();
        response.setStatusCode(jsonObject.getString("respstatus"));
        response.setSid(jsonObject.getString("msgid"));
        response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
                            && successCode.equals(response.getStatusCode()));
        response.setRemark(result);

        list.add(response);
        return list;
    }

    @Override
    public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
        try {
            logger.info("下行状态报告简码：{} =========={}", code(), report);

            JSONObject jsonObject = JSON.parseObject(report);
            if (MapUtils.isEmpty(jsonObject)) {
                return null;
            }

            List<SmsMtMessageDeliver> list = new ArrayList<>();
            SmsMtMessageDeliver response = new SmsMtMessageDeliver();

            response.setMsgId(jsonObject.getString("msgid"));
            response.setMobile(jsonObject.getString("mobile"));
            response.setCmcp(CMCP.local(jsonObject.getString("mobile")).getCode());
            response.setStatusCode(jsonObject.getString("status"));
            response.setStatus((StringUtils.isNotEmpty(response.getStatusCode())
                                && response.getStatusCode().equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
            response.setDeliverTime(dateNumberFormat("reporttime"));
            response.setCreateTime(new Date());
            response.setRemark(jsonObject.toJSONString());

            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("绿城状态报告解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);

            JSONObject jsonobj = JSONObject.parseObject(report);
            String msgId = jsonobj.getString("msgid");
            String destId = jsonobj.getString("destcode");
            String mobile = jsonobj.getString("mobile");
            String content = jsonobj.getString("msg");

            List<SmsMoMessageReceive> list = new ArrayList<>();

            SmsMoMessageReceive response = new SmsMoMessageReceive();
            response.setPassageId(passageId);
            response.setMsgId(msgId);
            response.setMobile(mobile);
            response.setContent(content);
            response.setDestnationNo(destId);
            response.setReceiveTime(dateNumberFormat("reporttime"));
            response.setCreateTime(new Date());
            response.setCreateUnixtime(response.getCreateTime().getTime());
            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("绿城上行解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "lvcheng";
    }

}
