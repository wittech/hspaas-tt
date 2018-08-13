package com.huashi.developer.prervice;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.constant.PassportConstant;
import com.huashi.developer.exception.ValidateException;

public class AbstractPrervice {

    protected final Logger                      logger              = LoggerFactory.getLogger(getClass());

    protected String sign(String originText, boolean isNeedEncode) throws ValidateException {
        try {
            if(isNeedEncode) {
                originText = URLEncoder.encode(originText, "UTF-8");
            }
            
            return SecurityUtil.md5Hex(originText);
        } catch (Exception e) {
            logger.error("生成签名失败", e);
            throw new ValidateException(ApiReponseCode.SERVER_EXCEPTION);
        }
    }
    
    protected String sign(String originText) throws ValidateException {
        return sign(originText, true);
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
