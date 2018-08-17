package com.huashi.developer.prervice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.LogUtils;
import com.huashi.common.vo.PaginationVo;
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

    private void checkSign(String appKey, int appId, String modeId, String title, Integer type, Integer status, Integer pageNo, Integer pageSize,
                           String timestamp, String sign) throws ValidateException {
        // (appKey+appId+modeId+title+type+status+ pageNo+pageSize+timestamp)
        
        String targetSign = appKey + appId;
        if(StringUtils.isNotEmpty(modeId)) {
            targetSign += modeId;
        }
        
        if(StringUtils.isNotEmpty(title)) {
            targetSign += title;
        }
        
        if(type != null) {
            targetSign += type;
        }
        
        if(status != null) {
            targetSign += status;
        }
        
        if(pageNo != null) {
            targetSign += pageNo;
        }
        
        if(pageSize != null) {
            targetSign += pageSize;
        }
        
        targetSign += timestamp;

        try {
            targetSign = sign(targetSign, false);
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

    private void verify(int appId, String modeId, String title, Integer type, Integer status, Integer pageNo,
                        Integer pageSize, String timestamp, String sign) throws ValidateException {

        checkTimestampExpired(timestamp);

        String appKey = getAppkey(appId);
        
        checkSign(appKey, appId, modeId, title, type, status, pageNo, pageSize, timestamp, sign);
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
                    finalContext = context + "【" + modeSign + "】";
                } else {
                    finalContext = "【" + modeSign + "】" + context;
                }
            }

            // 校验数据
            verify(appId, title, modeSign, context, location, type, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(finalContext);
            template.setRegexValue(modeSign);
            template.setNoticeMode(type);
            template.setMobile(location);
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
                    finalContext = context + "【" + modeSign + "】";
                } else {
                    finalContext = "【" + modeSign + "】" + context;
                }
            }

            // 校验数据
            verify(appId, modeId, title, modeSign, context, location, timestamp, sign);

            MessageTemplate template = new MessageTemplate();
            template.setId(Long.valueOf(modeId));
            template.setUserId(appId);
            template.setRemark(title);
            template.setContent(finalContext);
            template.setRegexValue(modeSign);
            template.setMobile(location);
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
            if (paramsMap.get("appId") == null) {
                throw new ValidateException(ApiReponseCode.REQUEST_EXCEPTION);
            }
            
            Integer appId = Integer.parseInt(paramsMap.get("appId")[0]);
            String modeId = null;
            if(paramsMap.containsKey("modeId")) {
                modeId = paramsMap.get("modeId")[0];
            }
            
            String title = null;
            if(paramsMap.containsKey("title")) {
                title = paramsMap.get("title")[0];
            }
            
            // type 模版短信类型，必填参数 1:行业短信 2:营销短信
            Integer type = null;
            if(paramsMap.containsKey("type")) {
                type = Integer.parseInt(paramsMap.get("type")[0]);
            }
            
            Integer status = null;
            if(paramsMap.containsKey("status")) {
                status = Integer.parseInt(paramsMap.get("status")[0]);
            }
            
            Integer pageNo = null;
            if(paramsMap.containsKey("pageNo")) {
                pageNo = Integer.parseInt(paramsMap.get("pageNo")[0]);
            }
            
            Integer pageSize = null;
            if(paramsMap.containsKey("pageSize")) {
                pageSize = Integer.parseInt(paramsMap.get("pageSize")[0]);
            }
            
            String timestamp = paramsMap.get("timestamp")[0];
            String sign = paramsMap.get("sign")[0];
            
            // 校验数据
            verify(appId, modeId, title, type, status, pageNo, pageSize, timestamp, sign);
            
            PaginationVo<MessageTemplate> pageList = smsTemplateService.findPage(appId, modeId, title, type, status, pageNo, pageSize);
            if(CollectionUtils.isEmpty(pageList.getList())) {
                return new SmsApiResponse();
            }
            
            return new SmsApiResponse(ApiReponseCode.SUCCESS.getCode(), ApiReponseCode.SUCCESS.getMessage(), pageResult(pageList.getList()));

        } catch (Exception e) {
            if (e instanceof ValidateException) {
                ValidateException ve = (ValidateException) e;
                return new SmsApiResponse(ve.getApiReponseCode().getCode(), ve.getApiReponseCode().getMessage());
            }

            logger.error("查询短信模板: [{}] 错误", JSON.toJSONString(paramsMap), e);
            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        }
    }
    
    private List<JSONObject> pageResult(List<MessageTemplate> list) {
//        {"appId":12,"modeId":"100001","title":"注册","modeSign":"测试1",
//        "context ":"亲爱的用户：${var1}，您好，欢迎注册本系统","location":1, 
//        "type":1,"status":1,"modifyTime":"2017-03-06 12:51:47"}
        List<JSONObject> pageResult = new ArrayList<>();
        for(MessageTemplate template : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", template.getUserId());
            jsonObject.put("modeId", template.getId().toString());
            jsonObject.put("title", template.getRemark());
            jsonObject.put("modeSign", template.getRegexValue());
             
            // 签名位置 0:尾部1:头部
            Integer location = Integer.parseInt(template.getMobile());
            String context  = template.getContent();
            jsonObject.put("location", location);
            if(location == 0) {
                context = context.substring(0, context.lastIndexOf("【"));
            } else {
                context = context.substring(context.lastIndexOf("】") + 1, context.length());
            }
            jsonObject.put("context", context);
            jsonObject.put("status", transferBdStatus(template.getStatus()));
            jsonObject.put("modifyTime", DateUtil.getSecondStr(template.getCreateTime()));
            
            pageResult.add(jsonObject);
        }
        
        return pageResult;
    }
    
    /**
     * TODO 转义大数据定义的状态 大数据： 1:审核中，2:审核通过，3:审核失败
     * 
     * @param status
     * @return
     */
    private static int transferBdStatus(Integer status) {
        if (status == null || status == ApproveStatus.WAITING.getValue()) {
            return 1;
        } else if (status == ApproveStatus.SUCCESS.getValue()) {
            return 2;
        } else {
            return 3;
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

        if (StringUtils.isEmpty(parameter)) {
            return content;
        }

        Map<String, String> varsMap = JSON.parseObject(parameter, new TypeReference<Map<String, String>>() {
        });
        try {

            for (Entry<String, String> entry : varsMap.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                content = content.replace("${" + paramName + "}", paramValue);
            }

            return content;
        } catch (Exception e) {
            LogUtils.error("根据表达式查询短信内容参数异常", e);
            return null;
        }
    }

    public static void main(String[] args) throws ValidateException {
        Integer appId = 132;
        String title = null;
        String modeId = null;
        Integer type = null;
        Integer status = null;
        Integer pageNo = null;
        Integer pageSize = null;
        String timestamp = "1534476532632";
        String sign = "73d904315c6f43e1293d73a243f3fa63";
        
//        264c08df8afe154d627fd32df341d1d4
//        264c08df8afe154d627fd32df341d1d4
        SmsTemplatePrervice previce = new SmsTemplatePrervice();
        
        previce.verify(appId, modeId, title, type, status, pageNo, pageSize, timestamp, sign);
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
