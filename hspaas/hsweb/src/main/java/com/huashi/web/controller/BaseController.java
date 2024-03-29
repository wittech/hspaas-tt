package com.huashi.web.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.huashi.common.vo.PaginationVo;
import com.huashi.common.vo.SessionUser;
import com.huashi.web.context.WebConstants;

/**
 * TODO Web基础类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年7月5日 下午3:06:23
 */
public class BaseController {

    public static final String    DEFAULT_CAPTCHA_MD5_CODE_KEY = "_HUASHI_MD5_CODE_";                // 图形验证码
    public static final String    DEFAULT_CAPTCHA_SMS_CODE_KEY = "_HUASHI_SMS_CODE_";                // 短信验证码

    protected final Logger        logger                       = LoggerFactory.getLogger(getClass());

    /**
     * 控制台前置
     */
    private static final String   ROUTE_CONSOLE                = "/console";

    @Resource
    protected HttpServletRequest  request;

    @Resource
    protected HttpServletResponse response;

    @Resource
    protected HttpSession         session;

    // 文件上传临时目录
    @Value("${tmp.store.directory}")
    protected String              tmpStoreDirectory;

    /**
     * TODO 获取Session用户信息
     * 
     * @return
     */
    protected SessionUser getSessionUser() {
        if (session.getAttribute(WebConstants.LOGIN_USER_SESSION_KEY) == null) {
            return null;
        }
        return (SessionUser) session.getAttribute(WebConstants.LOGIN_USER_SESSION_KEY);
    }

    /**
     * TODO 是否已登录
     * 
     * @return
     */
    protected boolean isLogin() {
        return getSessionUser() != null;
    }

    /**
     * TODO 获取登录人ID
     * 
     * @return
     */
    protected int getCurrentUserId() {
        if (getSessionUser() == null) {
            throw new RuntimeException("当前登录人信息为空，无法获取UserId.");
        }

        return getSessionUser().getId();
    }

    /**
     * TODO 获取客户端请求IP
     * 
     * @return
     */
    protected String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("127.0.0.1".equals(ip)) {
            InetAddress inet = null;
            try { // 根据网卡取本机配置的IP
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ip = inet.getHostAddress();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * TODO 设置session 手机号码验证码
     * 
     * @param mobile
     * @param code
     */
    protected void setSessionMobileSmsCode(String mobile, String code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile", mobile);
        jsonObject.put("code", code);
        jsonObject.put("time", System.currentTimeMillis());
        jsonObject.put("ip", getClientIp());

        session.setAttribute(DEFAULT_CAPTCHA_SMS_CODE_KEY, jsonObject);
    }

    /**
     * TODO 获取手机验证码
     * 
     * @param mobile
     * @return
     */
    protected JSONObject getSessionMobileSmsCode() {
        return (JSONObject) session.getAttribute(DEFAULT_CAPTCHA_SMS_CODE_KEY);
    }

    /**
     * 判断时间是否过期
     * 
     * @param time
     * @param sencodsLength
     * @return
     */
    protected boolean isExpired(Long time, Long sencodsLength) {
        return System.currentTimeMillis() - time > sencodsLength * 1000;
    }

    /**
     * TODO 是否允许发送短信
     * 
     * @return
     */
    protected boolean isAllowedSms() {
        return true;
    }

    protected String moduleInConsole(String module) {
        return ROUTE_CONSOLE + module;
    }

    /**
     * TODO 转换layui分页类
     * 
     * @param webPage
     * @return
     */
    protected <T> LayuiPage parseLayuiPage(PaginationVo<T> webPage) {
        return parseLayuiPage(webPage, "", "");
    }

    /**
     * TODO 转换成layui分页类
     * 
     * @param webPage
     * @param msg
     * @param code
     * @return
     */
    protected <T> LayuiPage parseLayuiPage(PaginationVo<T> webPage, String msg, String code) {
        LayuiPage layuiPage = new LayuiPage();
        layuiPage.setCode(code);
        layuiPage.setMsg(msg);
        
        if (webPage == null || CollectionUtils.isEmpty(webPage.getList())) {
            layuiPage.setCount(0);
            return layuiPage;
        }

        layuiPage.setCount(webPage.getTotalRecord());
        layuiPage.put(LayuiPage.DATA_NAME, webPage.getList());

        return layuiPage;

    }

    protected static class LayuiPage extends HashMap<String, Object> {

        private static final String CODE_NAME        = "code";
        private static final String MSG_NAME         = "msg";
        private static final String COUNT_NAME       = "count";
        private static final String DATA_NAME        = "data";

        private static final long   serialVersionUID = -3570363967052980674L;

        public void setCode(String code) {
            put(CODE_NAME, code);
        }

        public void setMsg(String msg) {
            put(MSG_NAME, msg);
        }

        public void setCount(int count) {
            put(COUNT_NAME, count);
        }

    }
}
