package com.huashi.exchanger.resolver.mms.http.maiyuan;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
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
import com.huashi.exchanger.resolver.mms.http.AbstractMmsPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.template.constant.MmsTemplateContext.MediaType;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;

/**
 * TODO 迈远平台
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月24日 下午7:38:53
 */
@Component
public class MmsMaiyuanPassageResolver extends AbstractMmsPassageResolver {

    /**
     * 参数分隔符
     */
    private static final String TEPE_CONTENT_SEPERATOR        = "|";

    /**
     * 多组数据分隔符
     */
    private static final String CONTENT_MULTI_GROUP_SEPERATOR = ";";

    /**
     * 发送彩信方法名
     */
    private static final String ACTION_SEND_MMS               = "send";

    /**
     * 彩信状态查询方法名
     */
    private static final String ACTION_MMS_STATUS             = "query";

    /**
     * 彩信延迟播放时间
     */
    private static final String DELAY_START_TIME              = "3";

    /**
     * 编码
     */
    private static final String ENCODEING_GB2312              = "GB2312";

    /**
     * 成功辨识
     */
    private static final String SUCCESS_CODE_FLAG             = ":";

    /**
     * 返回报告中是否含有消息体标识字段，如果含有如下字段，则标识有消息体，需要处理
     */
    private static final String REPORT_HAS_BODY_FLAG          = "taskid";

    @Override
    public List<ProviderSendResponse> send(MmsPassageParameter parameter, String mobile, String extNumber,
                                           String title, List<MmsMessageTemplateBody> bobies) {
        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            String result = HttpClientManager.post(parameter.getUrl(),
                                                   sendRequest(tparameter, title, bobies, mobile, extNumber));

            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("迈远彩信服务解析失败", e);
            throw new RuntimeException("迈远彩信服务解析失败");
        }
    }

    /**
     * TODO 发送彩信组装请求信息
     * 
     * @param tparameter
     * @param title
     * @param bobies
     * @param mobile
     * @param extNumber 扩展号
     * @return
     * @throws UnsupportedEncodingException
     */
    private Map<String, Object> sendRequest(TParameter tparameter, String title, List<MmsMessageTemplateBody> bobies,
                                            String mobile, String extNumber) throws UnsupportedEncodingException {

        Map<String, Object> params = new HashMap<>();
        params.put("action", ACTION_SEND_MMS);
        params.put("userid", tparameter.getString("userid"));
        params.put("account", tparameter.getString("account"));
        params.put("password", tparameter.getString("password"));
        params.put("mobile", mobile);
        params.put("title", title);
        params.put("content", content(bobies));

        return params;
    }

    /**
     * TODO 拼接content
     * 
     * @param bobies
     * @return
     * @throws UnsupportedEncodingException
     */
    private String content(List<MmsMessageTemplateBody> bobies) throws UnsupportedEncodingException {
        if (CollectionUtils.isEmpty(bobies)) {
            return StringUtils.EMPTY;
        }

        StringBuilder content = new StringBuilder();
        for (MmsMessageTemplateBody body : bobies) {
            content.append(DELAY_START_TIME).append(COMMON_SEPERATOR).append(body.getMediaName()).append(TEPE_CONTENT_SEPERATOR);
            if (MediaType.TEXT.getCode().equalsIgnoreCase(body.getMediaType())) {
                content.append(textContent(body.getData()));
            } else {
                content.append(urlEncode(body.getData()));
            }

            content.append(CONTENT_MULTI_GROUP_SEPERATOR);
        }

        if (StringUtils.isEmpty(content.toString())) {
            return StringUtils.EMPTY;
        }

        // 去掉最后一个分隔符
        return content.substring(0, content.length() - 1);
    }

    /**
     * TODO URL加码，防止BASE64字节HTTP 传输丢失
     * 
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    private String urlEncode(byte[] data) throws UnsupportedEncodingException {
        if (data == null) {
            throw new IllegalArgumentException("Byte data is null");
        }

        return Base64.encodeBase64String(data).replaceAll("\r|\n", "");
    }

    /**
     * TODO 文本内容转换
     * 
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    private String textContent(byte[] content) throws UnsupportedEncodingException {
        if (content == null) {
            throw new IllegalArgumentException("Content data is null");
        }

        return Base64.encodeBase64String(new String(content, DEFAULT_ENCODING).getBytes(ENCODEING_GB2312));
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

        List<ProviderSendResponse> list = new ArrayList<>();
        ProviderSendResponse response = new ProviderSendResponse();
        if (result.contains(SUCCESS_CODE_FLAG)) {
            response.setSuccess(true);
            response.setStatusCode(result.split(SUCCESS_CODE_FLAG)[0]);
            response.setSid(result.split(SUCCESS_CODE_FLAG)[1]);
            response.setRemark(result);
        } else {
            response.setSuccess(false);
            response.setStatusCode(result);
            response.setRemark(result);
        }
        list.add(response);
        return list;
    }

    /**
     * TODO 解析发送返回值
     * 
     * @param result
     * @param successCode
     * @return
     */
    private List<MmsMtMessageDeliver> deliverResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result) || !result.contains(REPORT_HAS_BODY_FLAG)) {
            return null;
        }

        logger.info("下行状态报告简码：{} =========={}", code(), result);

        successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

        List<MmsMtMessageDeliver> list = new ArrayList<>();
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
            MmsMtMessageDeliver response;
            for (Element et : data) {
                if (et.getChild("taskid", ns) == null) {
                    continue;
                }

                response = new MmsMtMessageDeliver();
                response.setMsgId(et.getChild("taskid", ns).getText());
                response.setMobile(et.getChild("mobile", ns).getText());
                response.setCmcp(CMCP.local(response.getMobile()).getCode());
                if (StringUtils.isNotEmpty(et.getChild("status", ns).getText())
                    && et.getChild("status", ns).getText().equalsIgnoreCase(successCode)) {
                    response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
                    response.setStatus(DeliverStatus.SUCCESS.getValue());
                } else {
                    response.setStatusCode(et.getChild("errorcode", ns).getText());
                    response.setStatus(DeliverStatus.FAILED.getValue());
                }
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
     * TODO 解析上行返回值
     * 
     * @param result
     * @param passageId
     * @return
     */
    private List<MmsMoMessageReceive> moResponse(String result, Integer passageId) {
        if (StringUtils.isEmpty(result) || !result.contains(REPORT_HAS_BODY_FLAG)) {
            return null;
        }

        logger.info("上行报告简码：{} =========={}", code(), result);

        List<MmsMoMessageReceive> list = new ArrayList<>();
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
            MmsMoMessageReceive response;

            for (Element et : data) {
                response = new MmsMoMessageReceive();
                response.setPassageId(passageId);
                response.setMsgId(et.getChild("taskid", ns).getText());
                response.setMobile(et.getChild("mobile", ns).getText());
                response.setContent(et.getChild("content", ns).getText());
                response.setDestnationNo(et.getChild("extno", ns).getText());
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

    /**
     * TODO 下行回执/上行彩信组装请求信息
     * 
     * @param tparameter
     * @return
     */
    private static Map<String, Object> request(TParameter tparameter) {
        Map<String, Object> params = new HashMap<>();
        params.put("userid", tparameter.getString("userid"));
        params.put("account", tparameter.getString("account"));
        params.put("password", tparameter.getString("password"));
        params.put("action", ACTION_MMS_STATUS);
        params.put("statusNum", 1000);

        return params;
    }

    @Override
    public List<MmsMtMessageDeliver> mtDeliver(TParameter tparameter, String url, String successCode) {
        try {
            String result = HttpClientManager.post(url, request(tparameter));

            // 解析返回结果并返回
            return deliverResponse(result, successCode);
        } catch (Exception e) {
            logger.error("迈远彩信报告解析失败", e);
            throw new RuntimeException("迈远彩信解析失败");
        }
    }

    @Override
    public List<MmsMoMessageReceive> moReceive(TParameter tparameter, String url, Integer passageId) {

        try {
            String result = HttpClientManager.post(url, request(tparameter));

            // 解析返回结果并返回
            return moResponse(result, passageId);
        } catch (Exception e) {
            logger.error("迈远彩信上行解析失败", e);
            throw new RuntimeException("迈远彩信解析失败");
        }
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "maiyuan";
    }

}
