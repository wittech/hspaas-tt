package com.huashi.developer.prervice;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.LogUtils;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.response.sms.SmsApiResponse;
import com.huashi.sms.template.context.TemplateContext.ApproveStatus;
import com.huashi.sms.template.domain.MessageTemplate;

@Service
public class SmsTemplatePrervice extends AbstractPrervice {

    private void checkSign(String appKey, int appId, String title, String modeSign, String context, String location,
                           int type, String timestamp, String sign) throws ValidateException {
        // (appKey+appId+ title+modeSign+context+location+type+timestamp)
        String targetSign = appKey + appId + title + modeSign + context + location + type + timestamp;

        try {
            targetSign = sign(targetSign);
            if (!targetSign.equals(sign)) {
                throw new ValidateException(ApiReponseCode.AUTHENTICATION_FAILED);
            }

        } catch (Exception e) {
            logger.error("校验签名失败", e);
            throw new ValidateException(ApiReponseCode.AUTHENTICATION_FAILED);
        }
    }

    /**
     * TODO 校验标题
     * 
     * @param title
     * @throws ValidateException
     */
    private void checkTitle(String title) throws ValidateException {
        if (StringUtils.isEmpty(title) || title.trim().length() >= 12) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_TITLE_NOT_MATCHED);
        }
    }

    /**
     * TODO 校验签名内容
     * 
     * @param sign
     * @throws ValidateException
     */
    private void checkSign(String sign) throws ValidateException {
        if (StringUtils.isEmpty(sign) || sign.trim().length() < 3 || sign.trim().length() > 8) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_SIGN_NOT_MATCHED);
        }
    }

    /**
     * TODO 校验模板内容
     * 
     * @param context
     * @throws ValidateException 
     */
    private void checkContext(String context) throws ValidateException {
        if (StringUtils.isEmpty(context) || context.trim().length() > 348) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_CONTEXT_NOT_MATCHED);
        }
    }

    /**
     * TODO 校验数据
     * 
     * @param appId
     * @param title
     * @param modeSign
     * @param context
     * @param location
     * @param type
     * @param timestamp
     * @param sign
     * @throws ValidateException 
     */
    private void verify(int appId, String title, String modeSign, String context, String location, int type,
                        String timestamp, String sign) throws ValidateException {

        checkTitle(title);

        checkSign(sign);

        checkContext(context);

        checkTimestampExpired(timestamp);

        String appKey = getAppkey(appId);

        checkSign(appKey, appId, title, modeSign, context, location, type, timestamp, sign);
    }

    /**
     * TODO 添加模板
     * 
     * @param // appId 应用编号，必填参数。 // title 模版标题，必填参数。小于12个字。 // modeSign 模版签名，必填参数。3～8个字。 // context
     * 模版内容，必填参数。模版签名+模版内容+2少于350个字。 // location 模版签名位置，必填参数 0:尾部1:头部 // type 模版短信类型，必填参数 1:行业短信 2:营销短信 // timestamp
     * 必填项，当前时间戳，如：1488786467 // sign 必填项，签名。(appKey+appId+ title+modeSign+context+location+type+ //
     * timestamp)进行urlencode编码后，再生成MD5转小写字母，appKey在平台获取。
     * @return
     */
    public SmsApiResponse addTemplate(Map<String, String[]> paramsMap) {
        try {
            int appId = Integer.parseInt(paramsMap.get("appId")[0]);
            String title = paramsMap.get("title")[0];
            // 签名位置
            String location = paramsMap.get("location")[0];
            // 签名值
            String modeSign = paramsMap.get("modeSign")[0];
            String context = paramsMap.get("context")[0];

            // type 模版短信类型，必填参数 1:行业短信 2:营销短信
            int type = Integer.parseInt(paramsMap.get("type")[0]);
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];

            if (StringUtils.isNotEmpty(modeSign)) {
                // 后置签名
                if (StringUtils.isNotEmpty(location) && "0".equals(location)) {
                    context = context + modeSign;
                } else {
                    context = modeSign + context;
                }
            }

            // 校验数据
            verify(appId, title, modeSign, context, location, type, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(context);
            template.setNoticeMode(type);
            boolean isOk = smsTemplateService.save(template);
            if (isOk) {
                return new SmsApiResponse();
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        } catch (Exception e) {
            if(e instanceof ValidateException) {
                ValidateException ve = (ValidateException)e;
                return new SmsApiResponse(ve.getApiReponseCode().getCode(), ve.getApiReponseCode().getMessage());
            }
            
            logger.error("添加短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        }
    }

    /**
     * TODO 修改模板信息
     * 
     * @param paramsMap
     * @return
     */
    public SmsApiResponse updateTemplate(Map<String, String[]> paramsMap) {
        try {
            String modeId = paramsMap.get("modeId")[0];
            if (StringUtils.isEmpty(modeId)) {
                throw new ValidateException(ApiReponseCode.TEMPLATE_INVALID);
            }

            int appId = Integer.parseInt(paramsMap.get("appId")[0]);
            String title = paramsMap.get("title")[0];
            // 签名位置
            String location = paramsMap.get("location")[0];
            // 签名值
            String modeSign = paramsMap.get("modeSign")[0];
            String context = paramsMap.get("context")[0];

            // type 模版短信类型，必填参数 1:行业短信 2:营销短信
            int type = Integer.parseInt(paramsMap.get("type")[0]);
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];

            if (StringUtils.isNotEmpty(modeSign)) {
                // 后置签名
                if (StringUtils.isNotEmpty(location) && "0".equals(location)) {
                    context = context + modeSign;
                } else {
                    context = modeSign + context;
                }
            }

            // 校验数据
            verify(appId, title, modeSign, context, location, type, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setId(Long.valueOf(modeId));
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(context);
            template.setNoticeMode(type);
            boolean isOk = smsTemplateService.update(template);
            if (isOk) {
                return new SmsApiResponse();
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        } catch (Exception e) {
            if(e instanceof ValidateException) {
                ValidateException ve = (ValidateException)e;
                return new SmsApiResponse(ve.getApiReponseCode().getCode(), ve.getApiReponseCode().getMessage());
            }
            
            logger.error("修改短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        }

    }

    // appId 应用编号，必填参数。
    // modeId 模版编号，必填参数。
    // timestamp 必填项，当前时间戳，如：1488786467
    // sign 必填项，签名。(appKey+appId+modeId+timestamp) 进行urlencode编码后，再生成MD5转小写字母，appKey在平台获取。

    public SmsApiResponse deleteTemplate(Map<String, String[]> paramsMap) {
        try {
            String modeId = paramsMap.get("modeId")[0];
            if (StringUtils.isEmpty(modeId)) {
                throw new ValidateException(ApiReponseCode.TEMPLATE_INVALID);
            }

            int appId = Integer.parseInt(paramsMap.get("appId")[0]);
            String title = paramsMap.get("title")[0];
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];

            // 校验数据
            verify(appId, title, timestamp, sign);

            boolean isOk = smsTemplateService.deleteById(Long.valueOf(modeId));
            if (isOk) {
                return new SmsApiResponse();
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);

        } catch (Exception e) {
            if(e instanceof ValidateException) {
                ValidateException ve = (ValidateException)e;
                return new SmsApiResponse(ve.getApiReponseCode().getCode(), ve.getApiReponseCode().getMessage());
            }
            
            logger.error("删除短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        }

    }

    public SmsApiResponse queryTemplate(Map<String, String[]> paramsMap) {
        try {

            String modeId = paramsMap.get("modeId")[0];
            if (StringUtils.isEmpty(modeId)) {
                throw new ValidateException(ApiReponseCode.TEMPLATE_INVALID);
            }

            int appId = Integer.parseInt(paramsMap.get("appId")[0]);
            String title = paramsMap.get("title")[0];
            // 签名位置
            String location = paramsMap.get("location")[0];
            // 签名值
            String modeSign = paramsMap.get("modeSign")[0];
            String context = paramsMap.get("context")[0];

            // type 模版短信类型，必填参数 1:行业短信 2:营销短信
            int type = Integer.parseInt(paramsMap.get("type")[0]);
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];
            // 校验数据
            verify(appId, title, modeSign, context, location, type, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setId(Long.valueOf(modeId));
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(context);
            template.setNoticeMode(type);
            boolean isOk = smsTemplateService.update(template);
            if (isOk) {
                return new SmsApiResponse();
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);

        } catch (Exception e) {
            if(e instanceof ValidateException) {
                ValidateException ve = (ValidateException)e;
                return new SmsApiResponse(ve.getApiReponseCode().getCode(), ve.getApiReponseCode().getMessage());
            }
            
            logger.error("查询短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        }
    }

    private void verify(int appId, String modeId, String timestamp, String sign) throws ValidateException {
        checkTimestampExpired(timestamp);

        String appKey = getAppkey(appId);

        String targetSign = appKey + appId + modeId + timestamp;
        try {
            targetSign = sign(targetSign);
            if (!targetSign.equals(sign)) {
                throw new ValidateException(ApiReponseCode.AUTHENTICATION_FAILED);
            }

        } catch (Exception e) {
            logger.error("校验签名失败", e);
            throw new ValidateException(ApiReponseCode.AUTHENTICATION_FAILED);
        }
    }

    /**
     * TODO 根据模板ID和模板变量值获取模板相关信息
     * 
     * @param id
     * @param vars
     * @return
     * @throws ValidateException
     */
    public MessageTemplate getById(Long id, String vars) throws ValidateException {
        MessageTemplate mode = smsTemplateService.get(id);
        if (mode == null) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_NOT_EXISTS);
        }

        if (ApproveStatus.SUCCESS.getValue() != mode.getStatus()) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_INVALID);
        }

        String content = beBornContentByRegex(mode.getContent(), vars);
        if (StringUtils.isEmpty(content)) {
            logger.error("根据模板ID: [" + id + "]和变量内容：[" + vars + "] ");
            throw new ValidateException(ApiReponseCode.SERVER_EXCEPTION);
        }

        mode.setContent(content);
        return mode;
    }

    /**
     * TODO 根据表达式提取内容中变量对应的值
     * 
     * @param content 短信内容
     * @param regex 表达式
     * @param paramSize 参数数量
     * @return
     */
    public static String beBornContentByRegex(String content, String vars) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        String[] varArr = vars.split("\\|");
        try {
            for (String v : varArr) {
                content = content.replaceFirst("#code#", v);
            }

            return content;
        } catch (Exception e) {
            LogUtils.error("根据表达式查询短信内容参数异常", e);
            return null;
        }
    }
}
