package com.huashi.exchanger.resolver.http.custom.mail139;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.HttpClientManager;
import com.huashi.exchanger.resolver.http.custom.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * TODO 139邮箱通道
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年11月22日 下午9:56:06
 */
@Component
public class Mail139PassageResolver extends AbstractPassageResolver {

    /**
     * 发送短信ACTION定义
     */
    private static final String SEND_ACTION             = "sendmail";

    /**
     * 版本号
     */
    private static final String VERSION                 = "2.0";

    /**
     * 邮箱后缀名
     */
    private static final String MAIL_SUFFIX             = "@139.com";

    /**
     * 报告格式
     */
    private static final String REPORT_FORMAT           = "xml";

    /**
     * 发送短信服务
     */
    private static final String SERVICE_TYPE            = "SMS";

    /**
     * 邮箱标题模板
     */
    private static final String EMAIL_TITLE_FORMAT      = "Send sms[%s] request from hspaas.cn";

    /**
     * API系统成功码（区别于短信回执码）
     */
    private static final String API_SYSTEM_SUCCESS_CODE = "000";

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientManager.postBody(parameter.getUrl(),
                                                       request(tparameter, mobile, content, extNumber,
                                                               parameter.getSmsTemplateId()));

            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("139邮箱通道发送失败", e);
            throw new RuntimeException("139邮箱通道发送失败[" + e.getMessage() + "]");
        }
    }

    /**
     * TODO 手机号码转邮箱格式(加后缀)
     * 
     * @param mobile
     * @return
     */
    private static String transformMobile2Mail(String mobile) {
        StringBuilder mail = new StringBuilder();
        String[] mobiles = mobile.split(MULTI_MOBILES_SEPERATOR);
        for (String m : mobiles) {
            mail.append(m + MAIL_SUFFIX + MULTI_MOBILES_SEPERATOR);
        }

        return mail.toString().substring(0, mail.length() - 1);
    }

    /**
     * TODO 获取当前时间戳
     * 
     * @return
     */
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * TODO 转码BASE64
     * 
     * @param content
     * @return
     */
    private String encodeText(String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("EncodeText is empty");
        }

        try {
            return Base64.encodeBase64String(text.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.warn("Text encode by '" + DEFAULT_ENCODING + "' failed, msg is '" + e.getMessage() + "'");
            return Base64.encodeBase64String(text.getBytes());
        }
    }

    /**
     * TODO 发送短信组装请求信息
     * 
     * @param tparameter
     * @param mobile
     * @param content 短信内容
     * @param extNumber 扩展号
     * @param passageTemplateId 通道方短信模板ID
     * @return
     */
    private String request(TParameter tparameter, String mobile, String content, String extNumber,
                           String passageTemplateId) {

        logger.info("通道模板参数===========" + tparameter);
        String appkey = tparameter.getString("account");
        String appSecret = tparameter.getString("password");
        String timestamp = getTimestamp();
        String receiverMail = transformMobile2Mail(mobile);
        String emailTitle = encodeText(String.format(EMAIL_TITLE_FORMAT, passageTemplateId));
        String finalContent = encodeText(content);
        String requestMethod = SEND_ACTION;
        String returnFormat = REPORT_FORMAT;
        String sendsmspriority = tparameter.getOrDefault("priority", 1).toString();
        String usernumber = mobile;
        String version = VERSION;

        String spnumber = tparameter.get("terminal_no") + (StringUtils.isNotBlank(extNumber) ? extNumber : "");

        // 计算签名
        String signature = signature(appkey, appSecret, finalContent, receiverMail, emailTitle, requestMethod,
                                     returnFormat, sendsmspriority, SERVICE_TYPE, spnumber, passageTemplateId,
                                     timestamp, finalContent, usernumber, version);

        return getXmlBody(appkey, appSecret, finalContent, receiverMail, emailTitle, requestMethod, returnFormat,
                          sendsmspriority, SERVICE_TYPE, passageTemplateId, timestamp, usernumber, version, signature,
                          spnumber);
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

        ProviderSendResponse response = new ProviderSendResponse();
        List<ProviderSendResponse> list = new ArrayList<>();
        StringReader read = new StringReader(result);
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element responseData = doc.getRootElement();

            String code = responseData.getChildText("resultcode");
            // 如果API系统鉴权失败，则不往下解析
            if (!API_SYSTEM_SUCCESS_CODE.equals(code)) {
                response.setStatusCode(code);
                response.setSendTime(DateUtil.getNow());
                response.setSuccess(false);
                response.setRemark(result);
                list.add(response);
                return list;
            }

            Element subData = responseData.getChild("return");

            // 邮箱返回码暂时不需要
            // String subretcode = subData.getChildText("subretcode");
            String smssendcode = subData.getChildText("smssendcode");
            String smsid = subData.getChildText("smsid");

            response.setStatusCode(smssendcode);
            response.setSid(smsid);
            response.setSendTime(DateUtil.getNow());
            response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
                                && successCode.equals(response.getStatusCode()));
            response.setRemark(result);
            list.add(response);

        } catch (Exception e) {
            logger.error("139邮箱解析发送回执失败, {}", result, e);
        }

        return list;
    }

    /**
     * TODO 签名
     * 
     * @param appkey
     * @param appSecret
     * @param emailContent
     * @param receiverMail
     * @param emailTitle
     * @param requestMethod
     * @param returnFormat
     * @param sendsmspriority
     * @param serviceType
     * @param spnumber
     * @param templateId
     * @param timestamp
     * @param title
     * @param usernumber
     * @param version
     * @return
     */
    private String signature(String appkey, String appSecret, String emailContent, String receiverMail,
                             String emailTitle, String requestMethod, String returnFormat, String sendsmspriority,
                             String serviceType, String spnumber, String templateId, String timestamp, String title,
                             String usernumber, String version) {

        StringBuilder signature = new StringBuilder();
        signature.append("app_key" + appkey);
        signature.append("app_secret" + appSecret);
        signature.append("content" + emailContent);
        signature.append("email_title" + emailTitle);
        signature.append("receiver_mail" + receiverMail);
        signature.append("request_method" + requestMethod);
        signature.append("return_format" + returnFormat);
        signature.append("sendsmspriority" + sendsmspriority);
        signature.append("serviceType" + serviceType);
        signature.append("spnumber" + spnumber);
        signature.append("templateId" + templateId);
        signature.append("timestamp" + timestamp);
        signature.append("title" + title);
        signature.append("usernumber" + usernumber);
        signature.append("version" + version);

        return DigestUtils.md5Hex(signature.toString());
    }

    /**
     * TODO 组装XML请求报文
     * 
     * @param appkey
     * @param appSecret
     * @param finalContent
     * @param receiverMail
     * @param emailTitle
     * @param requestMethod
     * @param returnFormat
     * @param sendsmspriority
     * @param serviceType
     * @param templateId
     * @param timestamp
     * @param usernumber
     * @param version
     * @param signature
     * @param spnumber
     * @return
     */
    private String getXmlBody(String appkey, String appSecret, String finalContent, String receiverMail,
                              String emailTitle, String requestMethod, String returnFormat, String sendsmspriority,
                              String serviceType, String templateId, String timestamp, String usernumber,
                              String version, String signature, String spnumber) {

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<sendmail>");
        xml.append("<request_method>" + requestMethod + "</request_method>");
        xml.append("<app_key>" + appkey + "</app_key>");
        xml.append("<app_secret>" + appSecret + "</app_secret>");
        xml.append("<sign_code>" + signature + "</sign_code>");
        xml.append("<timestamp>" + timestamp + "</timestamp>");
        xml.append("<version>" + version + "</version>");
        xml.append("<receiver_mail>" + receiverMail + "</receiver_mail>");
        xml.append("<email_title>" + emailTitle + "</email_title>");
        xml.append("<content>" + finalContent + "</content>");
        xml.append("<title>" + finalContent + "</title>");
        xml.append("<return_format>" + returnFormat + "</return_format>");
        xml.append("<usernumber>" + usernumber + "</usernumber>");
        xml.append("<sendsmspriority>" + sendsmspriority + "</sendsmspriority>");
        xml.append("<serviceType>" + serviceType + "</serviceType>");
        xml.append("<templateId>" + templateId + "</templateId>");
        xml.append("<spnumber>" + spnumber + "</spnumber>");
        xml.append("</sendmail>");

        return xml.toString();
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "mail139";
    }

    @Override
    public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
        logger.info("下行状态报告简码：{} =========={}", code(), report);

        if (StringUtils.isEmpty(report)) {
            return null;
        }

        try {
            successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

            List<SmsMtMessageDeliver> list = new ArrayList<>();

            StringReader read = new StringReader(report);
            InputSource source = new InputSource(read);
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(source);
            Element root = doc.getRootElement();

            List<Element> responseData = root.getChildren("responseData");
            SmsMtMessageDeliver response;
            for (Element rd : responseData) {
                response = new SmsMtMessageDeliver();
                response.setMsgId(rd.getChildText("MsgId"));
                response.setMobile(rd.getChildText("ReceiveNum"));
                response.setCmcp(CMCP.local(response.getMobile()).getCode());
                if (successCode.equalsIgnoreCase(rd.getChildText("SmsMsg"))) {
                    response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
                    response.setStatus(DeliverStatus.SUCCESS.getValue());
                } else {
                    response.setStatusCode(rd.getChildText("SendStatus") + ":" + rd.getChildText("SmsMsg"));
                    response.setStatus(DeliverStatus.FAILED.getValue());
                }
                response.setDeliverTime(rd.getChildText("ReceiveTime"));
                response.setRemark(JSON.toJSONString(response));
                response.setCreateTime(new Date());

                list.add(response);
            }

            return list;
        } catch (Exception e) {
            logger.error("139邮箱状态报告解析失败", e);
            throw new RuntimeException("Mt report from mail.139 failed[" + e.getMessage() + "]");
        }
    }
    
    @Override
    public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);

            List<SmsMoMessageReceive> list = new ArrayList<>();

            StringReader read = new StringReader(report);
            InputSource source = new InputSource(read);
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(source);
            Element root = doc.getRootElement();
            
            List<Element> responseData = root.getChildren("responseData");
            SmsMoMessageReceive response;
            for (Element rd : responseData) {
                response = new SmsMoMessageReceive();
                response.setPassageId(passageId);
                response.setMobile(rd.getChildText("UserNumber"));
                response.setContent(rd.getChildText("MoMsg"));
                response.setDestnationNo(rd.getChildText("SpNumber"));
                response.setReceiveTime(rd.getChildText("MoTime"));
                response.setCreateTime(new Date());
                response.setCreateUnixtime(response.getCreateTime().getTime());
                list.add(response);
            }

            return list;
        } catch (Exception e) {
            logger.error("139邮箱上行报告解析失败", e);
            throw new RuntimeException("Mo report from mail.139 failed[" + e.getMessage() + "]");
        }
    }

}
