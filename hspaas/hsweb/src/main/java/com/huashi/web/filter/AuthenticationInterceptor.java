package com.huashi.web.filter;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.huashi.common.util.LogUtils;
import com.huashi.common.vo.SessionUser;
import com.huashi.web.context.WebConstants;
import com.huashi.web.util.HttpUtil;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            printRequestInfo(request);

            HandlerMethod method = (HandlerMethod) handler;
            Object controllerObj = method.getBean();
            PermissionClear classPermission = controllerObj.getClass().getAnnotation(PermissionClear.class);
            PermissionClear methodPermission = method.getMethodAnnotation(PermissionClear.class);
            if (classPermission != null || methodPermission != null) {
                return true;
            }

            SessionUser user = (SessionUser) request.getSession().getAttribute(WebConstants.LOGIN_USER_SESSION_KEY);

            // 验证SESSION内是否存在有效的用户（未登录或者SESSION失效）
            if (user == null) {
                // ajax请求
                if (HttpUtil.isAjaxRequest(request)) {
                    response.setHeader("location", request.getContextPath() + WebConstants.SESSION_TIMEOUT_URL);
                    // response.sendError(HttpServletResponse.SC_FORBIDDEN, "您的登录已超时，请重新登录");
                    response.setHeader("sessionstatus", "timeout");
                } else {
                    response.sendRedirect(request.getContextPath() + WebConstants.SESSION_TIMEOUT_URL);
                }
                return false;
            }
        }

        return true;
    }

    /**
     * TODO 输出参数等信息
     * 
     * @param request
     */
    private void printRequestInfo(HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();
        StringBuilder sb = new StringBuilder("请求参数为--");
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String values = request.getParameter(name);
            sb.append(name + ":" + values + ", ");
        }
        LogUtils.debug("请求地址Start: " + request.getRequestURI() + ", " + sb.toString());
    }

}
