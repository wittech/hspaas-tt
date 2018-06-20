package com.huashi.developer.prervice;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.constant.PassportConstant;
import com.huashi.developer.exception.ValidateException;
import com.huashi.sms.template.service.ISmsTemplateService;

public class AbstractPrervice {

    protected final Logger                      logger              = LoggerFactory.getLogger(getClass());

    @Reference
    protected IUserDeveloperService             userDeveloperService;
    @Reference
    protected ISmsTemplateService               smsTemplateService;

    protected String sign(String originText) throws ValidateException {
        try {
            originText = URLEncoder.encode(originText, "UTF-8");
            return SecurityUtil.md5Hex(originText);
        } catch (Exception e) {
            logger.error("生成签名失败", e);
            throw new ValidateException(ApiReponseCode.SERVER_EXCEPTION);
        }
    }

    /**
     * TODO 判断用户时间戳是否过期
     * 
     * @param timestamp
     * @return
     */
    protected void checkTimestampExpired(String timestamp) throws ValidateException {
        try {
            boolean isSuccess = System.currentTimeMillis() - Long.valueOf(timestamp) <= PassportConstant.DEFAULT_EXPIRE_TIMESTAMP_MILLISECOND;
            if (isSuccess) {
                return;
            }

            throw new ValidateException(ApiReponseCode.TIMESTAMP_EXPIRED);
        } catch (Exception e) {
            logger.error("时间戳验证异常，{}", timestamp, e);
            throw new ValidateException(ApiReponseCode.TIMESTAMP_EXPIRED);
        }
    }

    /**
     * TODO 根据appid获取appkey(实际是我司的密钥，不是开发者编号)
     * 
     * @param appId
     * @return
     */
    protected String getAppkey(int appId) throws ValidateException{
        UserDeveloper userDeveloper = userDeveloperService.getByUserId(appId);
        if (userDeveloper == null) {
            throw new ValidateException(ApiReponseCode.APPKEY_INVALID);
        }

        if (userDeveloper.getStatus() == UserStatus.NO.getValue()) {
            throw new ValidateException(ApiReponseCode.API_DEVELOPER_INVALID);
        }

        return userDeveloper.getAppSecret();

    }
    
    /**
     * 
       * TODO 获取数据
       * 
       * @param name
       * @param paramsMap
       * @return
     */
    protected String getValue(String name , Map<String, String[]> paramsMap) {
        if(MapUtils.isEmpty(paramsMap)) {
            return null;
        }
        
        return paramsMap.get(name) != null ? paramsMap.get(name)[0] : null;
    }
    
}
