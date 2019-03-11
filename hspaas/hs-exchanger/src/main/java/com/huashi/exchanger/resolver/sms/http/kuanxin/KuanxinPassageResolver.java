package com.huashi.exchanger.resolver.sms.http.kuanxin;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
 * TODO 宽信通道
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年11月5日 下午8:50:26
 */
@Component
public class KuanxinPassageResolver extends AbstractPassageResolver {

    /**
     * 返回报告中是否含有消息体标识字段，如果含有如下字段，则标识有消息体，需要处理
     */
    private static final String REPORT_HAS_BODY_FLAG = "taskid";

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
            logger.error("宽信发送解析失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 发送短信组装请求信息
     * 
     * @param tparameter
     * @param mobile
     * @param content 短信内容
     * @param extNumber 扩展号
     * @return
     */
    private static Map<String, Object> sendRequest(TParameter tparameter, String mobile, String content,
                                                   String extNumber) {
        String timestamps = System.currentTimeMillis() + "";

        Map<String, Object> params = new HashMap<>();
        params.put("userid", tparameter.getString("userid"));
        params.put("ts", timestamps);
        params.put("sign",
                   DigestUtils.md5Hex(tparameter.getString("userid") + timestamps + tparameter.getString("apikey")));
        params.put("mobile", mobile);

        // URLEncode.encode(content,“utf-8”)
        params.put("msgcontent", content);
        params.put("extnum", extNumber == null ? "" : extNumber);

        // time（选填） 发送时间(为空表示立即发送，如果定时发送，则需要按yyyyMMddHHmmss格式，如：20110115105822)
        // messageid（选填）客户侧唯一消息ID,状态报告推送接口原样带回。

        return params;
    }

    /**
     * TODO 解析发送返回值 eg1: {“code”:”0”,”msg”:”成功”,”data”:{“tasked”:”T1011220639000170512059”}}
     * eg2：{“code”:”11”,”msg”:”签名信息鉴权失败”} eg3:{“code”:”10”,”msg”:”发送内容包含敏感词[枪支]”}
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

        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject == null) {
            return null;
        }

        List<ProviderSendResponse> list = new ArrayList<>();
        ProviderSendResponse response = new ProviderSendResponse();

        response.setStatusCode(jsonObject.getString("code"));
        response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
                            && successCode.equals(response.getStatusCode()));
        response.setRemark(result);

        // 如果状态码返回正确的才能解析到sid信息
        if (response.isSuccess()) {
            JSONObject taskJson = jsonObject.getJSONObject("data");
            if (MapUtils.isNotEmpty(taskJson) && taskJson.containsKey(REPORT_HAS_BODY_FLAG)) {
                response.setSid(taskJson.getString("taskid"));
            }
        }
        list.add(response);
        return list;
    }

    /**
     * 下行状态报告解析 eg :
     * [{"time":"20161012150400","taskid":"30cf60a756d444b986b9e3909e3b107a","code":"DELIVRD","msg":"成功","mobile"
     * :"13505718888"}]
     * 
     * @param report
     * @param successCode
     * @return
     * @see com.huashi.exchanger.resolver.AbstractMmsPassageResolver.custom.AbstractPassageResolver#mtDeliver(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
        try {
            logger.info("下行状态报告简码：{} =========={}", code(), report);

            JSONArray array = JSON.parseArray(report);
            if (CollectionUtils.isEmpty(array)) {
                return null;
            }

            List<SmsMtMessageDeliver> list = new ArrayList<>();
            SmsMtMessageDeliver response;
            for (Object object : array) {
                if (object == null) {
                    continue;
                }

                JSONObject jsonobj = (JSONObject) object;
                response = new SmsMtMessageDeliver();
                response.setMsgId(jsonobj.getString("taskid"));
                response.setMobile(jsonobj.getString("mobile"));
                response.setCmcp(CMCP.local(jsonobj.getString("mobile")).getCode());
                response.setStatusCode(jsonobj.getString("code"));
                response.setStatus((StringUtils.isNotEmpty(response.getStatusCode())
                                    && response.getStatusCode().equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));

                response.setDeliverTime(dateNumberFormat(jsonobj.getString("time")));
                response.setCreateTime(new Date());
                response.setRemark(jsonobj.toJSONString());

                list.add(response);
            }

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("宽信融合状态报告解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    /**
     * 短信上行推送报告解析 eg:[{"id":"9d9d0f4d1b864c129e299cc400c26306","time":"20161011115655","msgcontent":"测试上行短信","srcid":
     * "1069018889001","mobile":"15996480329"}]
     * 
     * @param report
     * @param passageId
     * @return
     * @see com.huashi.exchanger.resolver.AbstractMmsPassageResolver.custom.AbstractPassageResolver#moReceive(java.lang.String,
     * java.lang.Integer)
     */
    @Override
    public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);

            JSONArray array = JSON.parseArray(report);
            if (CollectionUtils.isEmpty(array)) {
                return null;
            }

            List<SmsMoMessageReceive> list = new ArrayList<>();
            SmsMoMessageReceive response;
            for (Object object : array) {
                response = new SmsMoMessageReceive();
                JSONObject jsonobj = (JSONObject) object;

                response.setPassageId(passageId);
                response.setMobile(jsonobj.getString("mobile"));
                response.setContent(jsonobj.getString("msgcontent"));
                response.setDestnationNo(jsonobj.getString("srcid"));
                response.setReceiveTime(dateNumberFormat(jsonobj.getString("time")));
                response.setCreateTime(new Date());
                response.setCreateUnixtime(response.getCreateTime().getTime());
                list.add(response);
            }

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("宽信融合上行解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "kuanxin";
    }
}
