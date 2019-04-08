package com.huashi.developer.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.developer.validator.AuthorizationValidator;

public class BasicApiSupport {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;
    
    @Autowired
    protected AuthorizationValidator passportValidator;

    @Override
    public String toString() {
        return JSON.toJSONString(request.getParameterMap());
    }

    /**
     * TODO 根据Header中传递值判断调用方式
     *
     * @return
     */
    protected int getAppType() {
        String appType = request.getHeader("apptype");
        if (StringUtils.isEmpty(appType)) {
            return AppType.DEVELOPER.getCode();
        }

        try {
            if (String.valueOf(AppType.WEB.getCode()).equals(appType)) {
                return AppType.WEB.getCode();
            }

            if (String.valueOf(AppType.BOSS.getCode()).equals(appType)) {
                return AppType.BOSS.getCode();
            }
        } catch (Exception e) {
        }

        return AppType.DEVELOPER.getCode();
    }
    
    /**
     * 
     * TODO 获取客户端请求IP
     * 
     * @return
     */
    protected String getClientIp() {
        String ip = request.getHeader("x-forwarded-for");
        if (logger.isDebugEnabled()) {
            logger.debug("x-forwarded-for = {}", ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (logger.isDebugEnabled()) {
                logger.debug("Proxy-Client-IP = {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (logger.isDebugEnabled()) {
                logger.debug("WL-Proxy-Client-IP = {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            if (logger.isDebugEnabled()) {
                logger.debug("X-Real-IP = {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (logger.isDebugEnabled()) {
                logger.debug("RemoteAddr-IP = {}", ip);
            }
        }
        if (StringUtils.isNotEmpty(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

}
