package com.huashi.developer.prervice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.LogUtils;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.response.sms.SmsApiResponse;
import com.huashi.sms.template.context.TemplateContext.ApproveStatus;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;

@Service
public class SmsTemplatePrervice extends AbstractPrervice {

    @Reference
    protected IUserDeveloperService userDeveloperService;
    @Reference
    protected ISmsTemplateService   smsTemplateService;

    /**
     * 参数定义前缀
     */
    private static final String     PARAM_NAME_PREFIX = "var";

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

    private void checkSign(String appKey, int appId, String modeId, String title, String modeSign, String context,
                           String location, String timestamp, String sign) throws ValidateException {
        // (appKey+appId+ title+modeSign+context+location+type+timestamp)
        String targetSign = appKey + appId + modeId + title + modeSign + context + location + timestamp;

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

        checkSign(modeSign);

        checkContext(context);

        checkTimestampExpired(timestamp);

        String appKey = getAppkey(appId);

        checkSign(appKey, appId, title, modeSign, context, location, type, timestamp, sign);
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
    private void verify(int appId, String modeId, String title, String modeSign, String context, String location,
                        String timestamp, String sign) throws ValidateException {

        checkTitle(title);

        checkSign(modeSign);

        checkContext(context);

        checkTimestampExpired(timestamp);

        String appKey = getAppkey(appId);

        checkSign(appKey, appId, modeId, title, modeSign, context, location, timestamp, sign);
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

            String finalContext = context;
            if (StringUtils.isNotEmpty(modeSign)) {
                // 后置签名
                if (StringUtils.isNotEmpty(location) && "0".equals(location)) {
                    finalContext = context + modeSign;
                } else {
                    finalContext = modeSign + context;
                }
            }

            finalContext = replaceVariable(finalContext);

            // 校验数据
            verify(appId, title, modeSign, context, location, type, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(finalContext);
            template.setNoticeMode(type);
            long id = smsTemplateService.save(template);
            if (id > 0) {

                List<JSONObject> rets = new ArrayList<>();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("modeId", id + "");
                rets.add(jsonObject);

                return new SmsApiResponse(ApiReponseCode.SUCCESS.getCode(), "成功", rets);
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        } catch (Exception e) {
            if (e instanceof ValidateException) {
                ValidateException ve = (ValidateException) e;
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

            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];

            String finalContext = context;
            if (StringUtils.isNotEmpty(modeSign)) {
                // 后置签名
                if (StringUtils.isNotEmpty(location) && "0".equals(location)) {
                    finalContext = context + modeSign;
                } else {
                    finalContext = modeSign + context;
                }
            }

            finalContext = replaceVariable(finalContext);

            // 校验数据
            verify(appId, modeId, title, modeSign, context, location, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setId(Long.valueOf(modeId));
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(finalContext);
            boolean isOk = smsTemplateService.update(template);
            if (isOk) {
                return new SmsApiResponse();
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        } catch (Exception e) {
            if (e instanceof ValidateException) {
                ValidateException ve = (ValidateException) e;
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
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];

            // 校验数据
            verify(appId, modeId, timestamp, sign);

            boolean isOk = smsTemplateService.deleteById(Long.valueOf(modeId));
            if (isOk) {
                return new SmsApiResponse();
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);

        } catch (Exception e) {
            if (e instanceof ValidateException) {
                ValidateException ve = (ValidateException) e;
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
            if (e instanceof ValidateException) {
                ValidateException ve = (ValidateException) e;
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
    public MessageTemplate getById(Long id, String parameter) throws ValidateException {
        MessageTemplate mode = smsTemplateService.get(id);
        if (mode == null) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_NOT_EXISTS);
        }

        if (ApproveStatus.SUCCESS.getValue() != mode.getStatus()) {
            throw new ValidateException(ApiReponseCode.TEMPLATE_INVALID);
        }

        String content = beBornContentByRegex(mode.getContent(), parameter);
        if (StringUtils.isEmpty(content)) {
            logger.error("根据模板ID: [" + id + "]和变量内容：[" + parameter + "] ");
            throw new ValidateException(ApiReponseCode.SERVER_EXCEPTION);
        }

        mode.setContent(content);
        return mode;
    }

    /**
     * TODO 根据表达式提取内容中变量对应的值
     * 
     * @param content 短信内容
     * @param parameter 表达式
     * @return
     */
    public static String beBornContentByRegex(String content, String parameter) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        
        if(StringUtils.isEmpty(parameter)) {
            return content;
        }

        Map<String, String> varsMap = JSON.parseObject(parameter, new TypeReference<Map<String, String>>() {
        });
        try {
            for (int i = 1; i <= varsMap.size(); i++) {
                content = content.replaceFirst("#code#", PARAM_NAME_PREFIX + i);
            }
            
            return content;
        } catch (Exception e) {
            LogUtils.error("根据表达式查询短信内容参数异常", e);
            return null;
        }
    }

    public static String replaceVariable(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        for (int i = 0; i <= 20; i++) {
            content = content.replace("${var" + i + "}", "#code#");
        }

        return content;
    }

    public static void main(String[] args) {
        String content = "h哈哈阿道夫${var1}开始看看IE${var3}";

        System.out.println(replaceVariable(content));
    }

    /**
     * TODO 根据appid获取appkey(实际是我司的密钥，不是开发者编号)
     * 
     * @param appId
     * @return
     */
    private String getAppkey(int appId) throws ValidateException {
        UserDeveloper userDeveloper = userDeveloperService.getByUserId(appId);
        if (userDeveloper == null) {
            throw new ValidateException(ApiReponseCode.APPKEY_INVALID);
        }

        if (userDeveloper.getStatus() == UserStatus.NO.getValue()) {
            throw new ValidateException(ApiReponseCode.API_DEVELOPER_INVALID);
        }

        return userDeveloper.getAppSecret();
    }
}
