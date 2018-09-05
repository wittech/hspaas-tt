package com.huashi.hsboss.web.interceptor;

import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.SystemConstant;
import com.huashi.hsboss.dto.UserSession;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.lang.reflect.Method;

/**
 * 登录认证拦截
 *
 * @author ym
 * @created_at 2016年6月22日下午2:12:00
 */
public class AuthInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        String controllerKey = inv.getControllerKey();
        if (controllerKey.startsWith("/account")) {
            inv.invoke();
        } else {
            if (controllerKey.startsWith("/common/file")) {  //文件上传暂时放行
                inv.invoke();
            } else {
                UserSession session = inv.getController().getSessionAttr(SystemConstant.USER_SESSION);
                if (session == null) {
                    inv.getController().redirect("/account/back");
//					BossUserService bossUserService = SpringBeanManager.getBean("bossUserService",BossUserService
// .class);
//					BossMenuService bossMenuService = SpringBeanManager.getBean("bossMenuService",BossMenuService
// .class);
//					BossUser bossUser = bossUserService.findByLogin("admin");
//					UserSession userSession = new UserSession();
//					userSession.setUserId(bossUser.getInt("id"));
//					userSession.setLoginName(bossUser.getStr("login_name"));
//					userSession.setRealName(bossUser.getStr("real_name"));
//					userSession.setLastLoginIp(bossUser.getStr("last_login_ip"));
//					userSession.setLastLoginTime(bossUser.getDate("last_login_time"));
//					userSession.setLoginIp(IpUtils.getClientIp(inv.getController().getRequest()));
//					userSession.setLoginTime(new Date());
//					List<UserMenu> menuList = bossMenuService.findAllMenu();
//					userSession.getMenuList().addAll(menuList);
//					inv.getController().getSession().setAttribute(SystemConstant.USER_SESSION, userSession);
//					inv.invoke();
                } else {
                    Method method = inv.getMethod();
                    if (checkOper(method, session)) {
                        inv.invoke();
                    } else {
                        ActionMode actionMode = method.getAnnotation(ActionMode.class);
                        if(actionMode != null && actionMode.type().equals(EnumConstant.ActionType.JSON)) {
                            BaseController baseController = (BaseController) inv.getController();
                            baseController.renderResultJson(false,"权限不足...");
                        } else {
                            inv.getController().redirect("/account/no_auth");
                        }

                    }
                }
            }
        }
    }

    private boolean checkOper(Method method, UserSession session) {
        AuthCode authCode = method.getAnnotation(AuthCode.class);
        if (authCode == null) {
            return false;
        }
        String[] operCodes = authCode.code();
        for (String code : operCodes) {
            if (session.doOper(code)) {
                return true;
            }
        }
        return false;
    }


}
