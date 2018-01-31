package com.huashi.exchanger.resolver.http.custom.xinchi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.HttpClientUtil;
import com.huashi.exchanger.resolver.http.custom.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * TODO 信驰通道处理
 * 
 * @contact 杭州信驰信息数据服务有限公司
 * @url http://www.xcapi.net
 * @author zhengying
 * @version V1.0
 * @date 2017年12月27日 下午10:07:37
 */
@Component
public class XinchiPassageResolver extends AbstractPassageResolver {
    
    /**
     * 默认前置签名
     */
    private static final String DEFAULT_SIGNATURE_POSITION = "FRONT";

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientUtil.post(parameter.getUrl(), sendRequest(tparameter, mobile, content, extNumber));

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("信驰发送解析失败", e);
            throw new RuntimeException("解析失败");
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
        Map<String, Object> params = new HashMap<>();
        params.put("apiKey", tparameter.getString("key"));
        params.put("apiAccount", tparameter.getString("password"));
        params.put("cells", mobile);
        params.put("content", content);
        params.put("style", tparameter.getOrDefault("position", DEFAULT_SIGNATURE_POSITION).toString());

        // 暂时不设置 signatureNo ,签名编号。选填。填写后会加上对应的签名。登录客户端可见
        // params.put("signatureNo", tparameter.getString("signatureNo") + extNumber);

        return params;
    }

    /**
     * TODO 解析发送返回值 
     * eg. {"errorCode": "", "overdue": false, "timeout": false, "message": "操作成功！", "success": true, "serialNo": "101117122700000634"  }
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

        response.setStatusCode(jsonObject.get("success") == null ? jsonObject.getString("errorCode") : jsonObject.get("success").toString());
        response.setSid(jsonObject.getString("serialNo"));
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

            JSONObject jsonobj = JSON.parseObject(report);
            if (MapUtils.isEmpty(jsonobj)) {
                return null;
            }

            List<SmsMtMessageDeliver> list = new ArrayList<>();
            SmsMtMessageDeliver response = new SmsMtMessageDeliver();
            response.setMsgId(jsonobj.getString("SerialNo"));
            response.setMobile(jsonobj.getString("Cell"));
            response.setCmcp(CMCP.local(jsonobj.getString("Cell")).getCode());
            response.setStatusCode(jsonobj.getString("Msg"));
            response.setStatus((StringUtils.isNotEmpty(response.getStatusCode())
                                && response.getStatusCode().equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
            response.setDeliverTime(jsonobj.getString("DoneTime"));
            response.setCreateTime(new Date());
            response.setRemark(jsonobj.toJSONString());

            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("信驰状态报告解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);

            JSONObject jsonobj = JSONObject.parseObject(report);
            String mobile = jsonobj.getString("Cell");
            String content = jsonobj.getString("Content");

            List<SmsMoMessageReceive> list = new ArrayList<>();

            SmsMoMessageReceive response = new SmsMoMessageReceive();
            response.setPassageId(passageId);
//            response.setMsgId(msgId);
            response.setMobile(mobile);
            response.setContent(content);
//            response.setDestnationNo(destId);
            response.setReceiveTime(DateUtil.getNow());
            response.setCreateTime(new Date());
            response.setCreateUnixtime(response.getCreateTime().getTime());
            list.add(response);

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("信驰上行解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public Object balance(Object param) {
        return 0;
    }

    @Override
    public String code() {
        return "xunchi";
    }
}
