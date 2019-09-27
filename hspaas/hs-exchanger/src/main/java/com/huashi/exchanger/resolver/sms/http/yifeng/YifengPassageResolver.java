package com.huashi.exchanger.resolver.sms.http.yifeng;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

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
 * 逸峰通道处理器
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年8月27日 下午5:13:16
 */
@Component
public class YifengPassageResolver extends AbstractPassageResolver {

    /**
     * 返回报告中是否含有消息体标识字段，如果含有如下字段，则标识有消息体，需要处理
     */
    private static final String REPORT_HAS_BODY_FLAG = "taskid";

    /**
     * 发送成功标识
     */
    private static final String STATUS_SUCCESS_MSG   = "成功";

    /**
     * 其他状态码描述
     */
    private static final String STATUS_OTHER_MSG     = "其他";

    /**
     * 错误码
     */
    private static final String COMMON_ERROR_CODE    = "MN:0000";

    /**
     * 其他错误码
     */
    private static final String OTHER_ERROR_CODE     = "MN:9999";

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientManager.post(parameter.getUrl(), null,
                                                   request(tparameter, mobile, content, extNumber),
                                                   HttpClientManager.DEFAULT_MAX_TOTAL,
                                                   HttpClientManager.DEFAULT_MAX_PER_ROUTE, 60000);

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("逸峰解析失败", e);
            throw new RuntimeException("逸峰解析失败");
        }
    }

    /**
     * 下行发送短信组装请求信息
     * 
     * @param tparameter
     * @param mobile 手机号码
     * @param content 短信内容
     * @param extNumber 扩展号码
     * @return
     */
    private static Map<String, Object> request(TParameter tparameter, String mobile, String content, String extNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("acctno", tparameter.getString("account"));
        params.put("password", tparameter.getString("password"));
        params.put("phone", mobile);
        params.put("message", content);
        params.put("extno", extNumber == null ? "" : extNumber);

        return params;
    }

    /**
     * 下行回执/上行短信组装请求信息
     * 
     * @param tparameter
     * @return
     */
    private static Map<String, Object> request(TParameter tparameter) {
        Map<String, Object> params = new HashMap<>();
        params.put("acctno", tparameter.getString("account"));
        params.put("password", tparameter.getString("password"));

        return params;
    }

    /**
     * 解析发送返回值
     * 
     * @param result
     * @param successCode
     * @return
     */
    private List<ProviderSendResponse> sendResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        List<ProviderSendResponse> list = new ArrayList<>();
        StringReader read = new StringReader(result);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();
            // 获得XML中的命名空间（XML中未定义可不写）
            Namespace ns = root.getNamespace();
            ProviderSendResponse response = new ProviderSendResponse();
            // response.setMobile(root.getChild("mobile", ns).getText());
            response.setStatusCode(root.getChild("returnstatus", ns).getText());
            response.setSid(root.getChild("taskID", ns).getText());
            response.setSendTime(DateUtil.getNow());
            response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
                                && successCode.equals(response.getStatusCode()));
            response.setRemark(result);
            list.add(response);

        } catch (JDOMException | IOException e) {
            logger.error("解析回执信息失败, {}", result, e);
        }
        return list;
    }

    /**
     * 获取最终的状态码信息
     * @param status
     * @param errorCode
     * @return
     */
    private String getResponseStatus(String status, String errorCode) {
        if(StringUtils.isEmpty(status)) {
            return COMMON_ERROR_CODE;
        } else if(status.contains(STATUS_SUCCESS_MSG)) {
            return COMMON_MT_STATUS_SUCCESS_CODE;
        } else if(status.contains(STATUS_OTHER_MSG)) {
            // 转义错误码为"其他"返回具体状态码（英文）
            return OTHER_ERROR_CODE;
        }else {
            if(StringUtils.isNotEmpty(errorCode)) {
                return errorCode;
            }

            return COMMON_ERROR_CODE;
        }
    }

    /**
     * 解析发送返回值
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
        StringReader read = new StringReader(result);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();
            // 得到根元素所有子元素的集合
            List<Element> data = root.getChildren();
            // 获得XML中的命名空间（XML中未定义可不写）
            Namespace ns = root.getNamespace();
            SmsMtMessageDeliver response;
            for (Element et : data) {
                if (et.getChild("taskid", ns) == null) {
                    continue;
                }

                response = new SmsMtMessageDeliver();
                response.setMsgId(et.getChild("taskid", ns).getText());
                response.setMobile(et.getChild("mobile", ns).getText());
                response.setCmcp(CMCP.local(response.getMobile()).getCode());

                // 状态转义
                String status = getResponseStatus(et.getChild("status", ns).getText(),
                        et.getChild("errorcode", ns).getText());
                response.setStatusCode(status);
                response.setStatus(COMMON_MT_STATUS_SUCCESS_CODE.equals(status) ? DeliverStatus.SUCCESS.getValue() :
                        DeliverStatus.FAILED.getValue());

                response.setRemark(String.format("taskid : %s, mobile : %s, status_code : %s", response.getMsgId(),
                                                 response.getMobile(), response.getStatusCode()));

                if (et.getChild("receivetime", ns) == null) {
                    response.setDeliverTime(DateUtil.getNow());
                } else {
                    response.setDeliverTime(et.getChild("receivetime", ns).getText().replaceAll("/", "-"));
                }

                response.setCreateTime(new Date());

                list.add(response);
            }

        } catch (JDOMException | IOException e) {
            logger.error("解析状态回执信息失败, {}", result, e);
        }
        return list;

    }

    /**
     * 解析上行返回值
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
        StringReader read = new StringReader(result);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();
            // 得到根元素所有子元素的集合
            List<Element> data = root.getChildren();
            // 获得XML中的命名空间（XML中未定义可不写）
            Namespace ns = root.getNamespace();
            SmsMoMessageReceive response;

            for (Element et : data) {
                response = new SmsMoMessageReceive();
                response.setPassageId(passageId);
                response.setMsgId(et.getChild("taskid", ns).getText());
                response.setMobile(et.getChild("mobile", ns).getText());
                response.setContent(et.getChild("content", ns).getText());
                if (et.getChild("receivetime", ns) == null) {
                    response.setReceiveTime(DateUtil.getNow());
                } else {
                    response.setReceiveTime(et.getChild("receivetime", ns).getText().replaceAll("/", "-"));
                }
                response.setCreateTime(new Date());
                response.setCreateUnixtime(response.getCreateTime().getTime());
                list.add(response);
            }

        } catch (JDOMException | IOException e) {
            logger.error("解析状态回执信息失败, {}", result, e);
        }
        return list;
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "yifeng";
    }

    @Override
    public List<SmsMtMessageDeliver> mtDeliver(TParameter tparameter, String url, String successCode) {
        try {
            String result = HttpClientManager.post(url, request(tparameter));

            // 解析返回结果并返回
            return deliverResponse(result, successCode);
        } catch (Exception e) {
            logger.error("逸峰短信报告解析失败", e);
            throw new RuntimeException("逸峰短信解析失败");
        }
    }

    @Override
    public List<SmsMoMessageReceive> moReceive(TParameter tparameter, String url, Integer passageId) {

        try {
            String result = HttpClientManager.post(url, request(tparameter));

            // 解析返回结果并返回
            return moResponse(result, passageId);
        } catch (Exception e) {
            logger.error("逸峰短信上行解析失败", e);
            throw new RuntimeException("逸峰短信解析失败");
        }
    }

}
