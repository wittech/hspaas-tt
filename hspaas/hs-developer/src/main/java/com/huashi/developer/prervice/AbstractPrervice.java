package com.huashi.developer.prervice;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.constant.PassportConstant;
import com.huashi.developer.response.sms.SmsApiResponse;
import com.huashi.sms.template.service.ISmsTemplateService;

public class AbstractPrervice {

    protected final Logger                      logger              = LoggerFactory.getLogger(getClass());

    /**
     * 短信调用回执
     */
    protected final ThreadLocal<SmsApiResponse> smsApiResponseLocal = new ThreadLocal<>();

    @Reference
    protected IUserDeveloperService             userDeveloperService;
    @Reference
    protected ISmsTemplateService               smsTemplateService;

    protected String sign(String originText) {
        try {
            originText = URLEncoder.encode(originText, "UTF-8");
            return SecurityUtil.md5Hex(originText);
        } catch (Exception e) {
            logger.error("生成签名失败", e);
            return null;
        }
    }

    /**
     * TODO 判断用户时间戳是否过期
     * 
     * @param timestamp
     * @return
     */
    protected void checkTimestampExpired(String timestamp) {
        try {
            boolean isSuccess = System.currentTimeMillis() - Long.valueOf(timestamp) <= PassportConstant.DEFAULT_EXPIRE_TIMESTAMP_MILLISECOND;
            if (isSuccess) {
                return;
            }

            smsApiResponseLocal.get().setResponse(ApiReponseCode.TIMESTAMP_EXPIRED);
        } catch (Exception e) {
            logger.error("时间戳验证异常，{}", timestamp, e);
            smsApiResponseLocal.get().setResponse(ApiReponseCode.TIMESTAMP_EXPIRED);
        }
    }

    protected void resetLocal() {
        smsApiResponseLocal.remove();
        smsApiResponseLocal.set(new SmsApiResponse());
    }

    /**
     * TODO 根据appid获取appkey(实际是我司的密钥，不是开发者编号)
     * 
     * @param appId
     * @return
     */
    protected String getAppkey(int appId) {
        UserDeveloper userDeveloper = userDeveloperService.getByUserId(appId);
        if (userDeveloper == null) {
            smsApiResponseLocal.get().setResponse(ApiReponseCode.APPKEY_INVALID);
            return null;
        }

        if (userDeveloper.getStatus() == UserStatus.NO.getValue()) {
            smsApiResponseLocal.get().setResponse(ApiReponseCode.APPKEY_INVALID);
            return null;
        }

        return userDeveloper.getAppSecret();

    }
    
    /**
     * TODO 校验结果是否正确
     * 
     * @return
     */
    protected boolean isRight() {
        return ApiReponseCode.SUCCESS.getCode().equals(smsApiResponseLocal.get().getCode());
    }

}
