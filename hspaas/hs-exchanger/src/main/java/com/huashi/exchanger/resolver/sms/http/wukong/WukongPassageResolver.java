package com.huashi.exchanger.resolver.sms.http.wukong;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
 * TODO 大查柜（上海）网络有限公司 悟空短信平台接口实现
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年5月10日 下午10:29:47
 */
@Component
public class WukongPassageResolver extends AbstractPassageResolver {

    /**
     * 返回报告中是否含有消息体标识字段，如果含有如下字段，则标识有消息体，需要处理
     */
    private static final String REPORT_HAS_BODY_FLAG = "phone_no";

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientManager.post(parameter.getUrl(), request(tparameter, mobile, content, extNumber));

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    /**
     * TODO 获取当前时间戳UNIXTIME（秒）
     * 
     * @return
     */
    private static String getCurrentUnixSencond() {
        return System.currentTimeMillis() / 1000 + "";
    }

    /**
     * TODO 下行发送短信组装请求信息
     * 
     * @param tparameter
     * @param mobile 手机号码
     * @param content 短信内容
     * @param extNumber 扩展号码
     * @return
     */
    private static Map<String, Object> request(TParameter tparameter, String mobile, String content, String extNumber) {
        String timestamps = getCurrentUnixSencond();
        Map<String, Object> params = new HashMap<>();
        params.put("app_key", tparameter.getString("account"));
        params.put("check", signature(tparameter.getString("account"), tparameter.getString("password"), timestamps));
        params.put("phone_no", mobile);
        params.put("msg", content);
        // 扩展号，目前支持1-6位
        params.put("sub_port", extNumber == null ? "" : extNumber);
        params.put("timestamp", timestamps);

        return params;
    }

    /**
     * TODO 下行回执/上行短信组装请求信息
     * 
     * @param tparameter
     * @return
     */
    private static Map<String, Object> request(TParameter tparameter) {
        String timestamps = getCurrentUnixSencond();
        Map<String, Object> params = new HashMap<>();
        params.put("app_key", tparameter.getString("account"));
        params.put("check", signature(tparameter.getString("account"), tparameter.getString("password"), timestamps));
        params.put("timestamp", timestamps);

        return params;
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
        if (code == null || StringUtils.isEmpty(code.toString())) {
            return null;
        }

        List<ProviderSendResponse> list = new ArrayList<>();
        ProviderSendResponse response = new ProviderSendResponse();
        response.setStatusCode(code.toString());
        response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
                            && successCode.equals(response.getStatusCode()));
        response.setRemark(result);

        Object obj = m.get("data");
        if (obj != null && StringUtils.isNotEmpty(obj.toString())) {
            JSONObject jsonObj = (JSONObject) obj;
            response.setSid(jsonObj.getString("task_id"));
        }

        list.add(response);
        return list;
    }

    /**
     * TODO 获取状态错误描述信息
     * 
     * @param code
     * @return
     */
    private static String getStatusDes(String code) {
        if (StringUtils.isEmpty(code)) {
            return "99：其他";
        }

        switch (code) {
            case "1":
                return "1:空号";
            case "2":
                return "2：关机停机";
            case "3":
                return "3：发送频率过高";
            case "4":
                return "4：签名无效";
            case "5":
                return "5：黑词";
            case "6":
                return "6：黑名单";
            case "7":
                return "7：短信内容有误";
            case "8":
                return "8：必须包含退订";
            default:
                return "99：其他";
        }
    }

    /**
     * TODO 解析发送返回值
     * 
     * @param result
     * @param successCode
     * @return
     */
    private List<SmsMtMessageDeliver> deliverResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result) || !result.contains(REPORT_HAS_BODY_FLAG)) {
            return null;
        }

        logger.info("下行状态报告简码：{} =========={}", code(), result);

        successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

        List<SmsMtMessageDeliver> list = new ArrayList<>();
        try {

            Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            Object code = m.get("code");
            if (code == null || StringUtils.isEmpty(code.toString())) {
                return null;
            }

            Object obj = m.get("data");
            if (obj == null || StringUtils.isEmpty(obj.toString())) {
                return null;
            }

            List<Map<String, Object>> data = JSON.parseObject(m.get("data").toString(),
                                                              new TypeReference<List<Map<String, Object>>>() {
                                                              });

            SmsMtMessageDeliver response;
            for (Map<String, Object> et : data) {
                response = new SmsMtMessageDeliver();
                response.setMsgId(et.getOrDefault("task_id", "").toString());
                response.setMobile(et.getOrDefault("phone_no", "").toString());
                response.setCmcp(CMCP.local(response.getMobile()).getCode());
                if (successCode.equalsIgnoreCase(et.get("receive_code").toString())) {
                    response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
                    response.setStatus(DeliverStatus.SUCCESS.getValue());
                } else {
                    response.setStatusCode(getStatusDes(et.getOrDefault("receive_code", "").toString()));
                    response.setStatus(DeliverStatus.FAILED.getValue());
                }
                response.setRemark(JSON.toJSONString(et));
                response.setDeliverTime(unixtimeStamp2Date(et.get("receive_time")));
                response.setCreateTime(new Date());

                list.add(response);
            }

        } catch (Exception e) {
            logger.error("悟空解析状态回执信息失败, {}", result, e);
        }
        return list;

    }

    /**
     * TODO 短信签名
     * 
     * @param appkey
     * @param password
     * @param timestamps
     * @return
     */
    private static String signature(String appkey, String password, String timestamps) {
        return DigestUtils.md5Hex(password + appkey + timestamps);
    }

    /**
     * TODO 解析上行返回值
     * 
     * @param result
     * @param passageId
     * @return
     */
    private List<SmsMoMessageReceive> moResponse(String result, Integer passageId) {
        if (StringUtils.isEmpty(result) || !result.contains(REPORT_HAS_BODY_FLAG)) {
            return null;
        }

        logger.info("上行报告简码：{} =========={}", code(), result);

        List<SmsMoMessageReceive> list = new ArrayList<>();
        Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
        });

        Object obj = m.get("data");
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            return null;
        }

        List<Map<String, Object>> data = JSON.parseObject(m.get("data").toString(),
                                                          new TypeReference<List<Map<String, Object>>>() {
                                                          });
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }

        SmsMoMessageReceive response;
        for (Map<String, Object> my : data) {
            response = new SmsMoMessageReceive();
            response.setPassageId(passageId);
            response.setMobile(my.getOrDefault("phone_no", "").toString());
            response.setContent(my.getOrDefault("msg", "").toString());
            response.setReceiveTime(unixtimeStamp2Date(my.get("reply_time")));
            response.setCreateTime(new Date());
            response.setCreateUnixtime(response.getCreateTime().getTime());
            list.add(response);
        }
        return list;
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "wukong";
    }

    @Override
    public List<SmsMtMessageDeliver> mtDeliver(TParameter tparameter, String url, String successCode) {
        try {
            String result = HttpClientManager.post(url, request(tparameter));

            // 解析返回结果并返回
            return deliverResponse(result, successCode);
        } catch (Exception e) {
            logger.error("解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public List<SmsMoMessageReceive> moReceive(TParameter tparameter, String url, Integer passageId) {

        try {
            String result = HttpClientManager.post(url, request(tparameter));

            // 解析返回结果并返回
            return moResponse(result, passageId);
        } catch (Exception e) {
            logger.error("解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

}
