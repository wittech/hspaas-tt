package com.huashi.developer.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.developer.util.IpUtil;

public class BasicApiSupport {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;
    
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
     * TODO 获取客户端IP
     *
     * @return
     */
    protected String getClientIp() {
        return IpUtil.getClientIp(request);
    }

}
