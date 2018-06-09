package com.huashi.developer.prervice;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.response.sms.SmsApiResponse;
import com.huashi.sms.template.domain.MessageTemplate;

@Service
public class SmsTemplatePrervice extends AbstractPrervice{

    private void checkSign(String appKey, int appId, String title, String modeSign, String context, String location,
                           int type, String timestamp, String sign) {
        // (appKey+appId+ title+modeSign+context+location+type+timestamp)
        String targetSign = appKey + appId + title + modeSign + context + location + type + timestamp;

        try {
            targetSign = sign(targetSign);
            if (!targetSign.equals(sign)) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.AUTHENTICATION_FAILED);
                return;
            }

        } catch (Exception e) {
            logger.error("校验签名失败", e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.AUTHENTICATION_FAILED);
        }
    }

    /**
     * TODO 校验标题
     * 
     * @param title
     */
    private void checkTitle(String title) {
        if (StringUtils.isEmpty(title) || title.trim().length() >= 12) {
            smsApiResponseLocal.get().setResponse(ApiReponseCode.TEMPLATE_TITLE_NOT_MATCHED);
        }
    }

    /**
     * TODO 校验签名内容
     * 
     * @param sign
     */
    private void checkSign(String sign) {
        if (StringUtils.isEmpty(sign) || sign.trim().length() < 3 || sign.trim().length() > 8) {
            smsApiResponseLocal.get().setResponse(ApiReponseCode.TEMPLATE_SIGN_NOT_MATCHED);
        }
    }

    /**
     * TODO 校验模板内容
     * 
     * @param context
     */
    private void checkContext(String context) {
        if (StringUtils.isEmpty(context) || context.trim().length() > 348) {
            smsApiResponseLocal.get().setResponse(ApiReponseCode.TEMPLATE_CONTEXT_NOT_MATCHED);
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
     */
    private void verify(int appId, String title, String modeSign, String context, String location, int type,
                        String timestamp, String sign) {

        checkTitle(title);
        if (!isRight()) {
            return;
        }

        checkSign(sign);
        if (!isRight()) {
            return;
        }

        checkContext(context);
        if (!isRight()) {
            return;
        }

        checkTimestampExpired(timestamp);
        if (!isRight()) {
            return;
        }

        String appKey = getAppkey(appId);
        if (!isRight()) {
            return;
        }

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
        resetLocal();

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
            if (!isRight()) {
                return smsApiResponseLocal.get();
            }

            MessageTemplate template = new MessageTemplate();
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(context);
            template.setNoticeMode(type);
            boolean isOk = smsTemplateService.save(template);
            if (isOk) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.SUCCESS);
                return smsApiResponseLocal.get();
            }

        } catch (Exception e) {
            logger.error("添加短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.SERVER_EXCEPTION);
        }

        return smsApiResponseLocal.get();
    }

    /**
     * TODO 修改模板信息
     * 
     * @param paramsMap
     * @return
     */
    public SmsApiResponse updateTemplate(Map<String, String[]> paramsMap) {
        resetLocal();

        try {
            String modeId = paramsMap.get("modeId")[0];
            if (StringUtils.isEmpty(modeId)) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.TEMPLATE_INVALID);
                return smsApiResponseLocal.get();
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
            if (!isRight()) {
                return smsApiResponseLocal.get();
            }

            MessageTemplate template = new MessageTemplate();
            template.setId(Long.valueOf(modeId));
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(context);
            template.setNoticeMode(type);
            boolean isOk = smsTemplateService.update(template);
            if (isOk) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.SUCCESS);
                return smsApiResponseLocal.get();
            }

        } catch (Exception e) {
            logger.error("修改短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.SERVER_EXCEPTION);
        }

        return smsApiResponseLocal.get();
    }

    // appId 应用编号，必填参数。
    // modeId 模版编号，必填参数。
    // timestamp 必填项，当前时间戳，如：1488786467
    // sign 必填项，签名。(appKey+appId+modeId+timestamp) 进行urlencode编码后，再生成MD5转小写字母，appKey在平台获取。

    public SmsApiResponse deleteTemplate(Map<String, String[]> paramsMap) {
        resetLocal();

        try {
            String modeId = paramsMap.get("modeId")[0];
            if (StringUtils.isEmpty(modeId)) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.TEMPLATE_INVALID);
                return smsApiResponseLocal.get();
            }

            int appId = Integer.parseInt(paramsMap.get("appId")[0]);
            String title = paramsMap.get("title")[0];
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];
            
            // 校验数据
            verify(appId, title, timestamp, sign);
            if (!isRight()) {
                return smsApiResponseLocal.get();
            }

            boolean isOk = smsTemplateService.deleteById(Long.valueOf(modeId));
            if (isOk) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.SUCCESS);
                return smsApiResponseLocal.get();
            }

        } catch (Exception e) {
            logger.error("删除短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.SERVER_EXCEPTION);
        }

        return smsApiResponseLocal.get();
    }

    public SmsApiResponse queryTemplate(Map<String, String[]> paramsMap) {
        resetLocal();

        try {
            
            String modeId = paramsMap.get("modeId")[0];
            if (StringUtils.isEmpty(modeId)) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.TEMPLATE_INVALID);
                return smsApiResponseLocal.get();
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
            if (!isRight()) {
                return smsApiResponseLocal.get();
            }
            
            
//            appId   应用编号，必填参数。
//            modeId  模版编号，选填参数。
//            可批量查询，用“,”分隔，个数不得超过100个。
//            如：100001, 100002, 100003
//            title   模版标题，选填参数
//            type    模版短信类型，选填参数 1:行业短信 2:营销短信
//            status  模版状态，选填参数。1:审核中，2:审核通过，3:审核失败
//            pageNo  页数，选填参数。默认第1页。
//            pageSize    单页条数，选填参数。默认单页10条。
//            timestamp   必填项，当前时间戳，如：1488786467
//            sign    必填项，签名。(appKey+appId+modeId+title+type+status+ pageNo+pageSize+timestamp) 进行urlencode编码后，再生成MD5转小写字母，appKey在平台获取。


            MessageTemplate template = new MessageTemplate();
            template.setId(Long.valueOf(modeId));
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(context);
            template.setNoticeMode(type);
            boolean isOk = smsTemplateService.update(template);
            if (isOk) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.SUCCESS);
                return smsApiResponseLocal.get();
            }

        } catch (Exception e) {
            logger.error("修改短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.SERVER_EXCEPTION);
        }

        return smsApiResponseLocal.get();
    }

    private void verify(int appId, String modeId, String timestamp, String sign) {
        checkTimestampExpired(timestamp);
        if (!isRight()) {
            return;
        }

        String appKey = getAppkey(appId);
        if (!isRight()) {
            return;
        }

        String targetSign = appKey + appId + modeId + timestamp;
        try {
            targetSign = sign(targetSign);
            if (!targetSign.equals(sign)) {
                smsApiResponseLocal.get().setResponse(ApiReponseCode.AUTHENTICATION_FAILED);
                return;
            }

        } catch (Exception e) {
            logger.error("校验签名失败", e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.AUTHENTICATION_FAILED);
        }
    }

}
