package com.huashi.hsboss.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.SystemConstant;
import com.huashi.hsboss.dto.UserMenu;
import com.huashi.hsboss.dto.UserSession;
import com.huashi.hsboss.model.boss.BossUser;
import com.huashi.hsboss.service.boss.BossMenuService;
import com.huashi.hsboss.service.boss.BossUserService;
import com.huashi.hsboss.util.CaptcharRender;
import com.huashi.hsboss.util.GoogleAuthenticator;
import com.huashi.hsboss.util.IpUtils;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 系统帐户登录、登出
 * 
 * @author ym
 * @created_at 2016年6月28日下午6:00:29
 */
@ControllerBind(controllerKey = "/account")
public class AccountController extends BaseController {

    @BY_NAME
    private BossUserService bossUserService;
    @BY_NAME
    private BossMenuService bossMenuService;

    /**
     * TODO 登录页面
     */
    public void index() {
    }

    /**
     * TODO 登录授权校验
     */
    public void login() {
        String authCode = getPara("authCode");
        String sessionCode = getSessionAttr(SystemConstant.SESSION_VALIDATE_CODE);
        if (!authCode.toUpperCase().equals(sessionCode)) {
            renderResultJson(false, "验证码输入不正确", "authCode");
            return;
        }

        String loginName = getPara("loginName");
        String password = getPara("password");

        BossUser bossUser = bossUserService.findByLogin(loginName);
        if (bossUser == null) {
            renderResultJson(false, "用户名或密码不正确！", "loginName");
            return;
        }

        // 校验密码是否正确
        String md5pwd = DigestUtils.md5Hex(password);
        if (!md5pwd.toUpperCase().equals(bossUser.getStr("password").toUpperCase())) {
            renderResultJson(false, "用户名或密码不正确！", "password");
            return;
        }

        if (!mfaValidate(bossUser.getStr("mfa"))) {
            renderResultJson(false, "动态指令校验失败！", "mfa");
            return;
        }

        initLoginSettings(bossUser);

        renderResultJson(true, "登录成功，正在跳转...", "");
    }

    /**
     * TODO mfa鉴权
     * 
     * @param userMfa
     * @return
     */
    private boolean mfaValidate(String userMfa) {
        // 如果MFA开关设置为不开启则不需要校验
        if (!SystemConstant.USER_AUTH_MFA) {
            return true;
        }

        String inputMfa = getPara("mfa");
        if (StringUtils.isEmpty(inputMfa)) {
            return false;
        }

        Boolean auth = GoogleAuthenticator.authcode(inputMfa, userMfa);
        if (auth == null || !auth) {
            return false;
        }

        return true;
    }

    /**
     * TODO 初始化登录后配置信息
     * 
     * @param bossUser
     */
    private void initLoginSettings(BossUser bossUser) {
        UserSession userSession = new UserSession();
        userSession.setUserId(bossUser.getInt("id"));
        userSession.setLoginName(bossUser.getStr("login_name"));
        userSession.setRealName(bossUser.getStr("real_name"));
        userSession.setLastLoginIp(bossUser.getStr("last_login_ip"));
        userSession.setLastLoginTime(bossUser.getDate("last_login_time"));
        userSession.setLoginIp(IpUtils.getClientIp(getRequest()));
        userSession.setLoginTime(new Date());
        Integer superFlag = bossUser.getInt("super_flag");
        userSession.setSuperAdmin((superFlag != null && superFlag == 1));
        List<UserMenu> menuList = null;
        if (userSession.isSuperAdmin()) {
            menuList = bossMenuService.getAllMenu();
        } else {
            menuList = bossMenuService.getUserMenuById(userSession.getUserId());
            userSession.getOperSet().addAll(bossMenuService.getOperCodeByUserId(userSession.getUserId()));
        }

        userSession.getMenuList().addAll(menuList);
        getSession().setAttribute(SystemConstant.USER_SESSION, userSession);

        bossUser.set("last_login_ip", userSession.getLoginIp());
        bossUser.set("last_login_time", userSession.getLoginTime());
        bossUser.update();
    }

    /**
     * TODO 生成图形验证码
     * 
     * @throws IOException
     */
    public void validate_code() throws IOException {
        String code = CaptcharRender.generateVerifyCode(4);
        setSessionAttr(SystemConstant.SESSION_VALIDATE_CODE, code);
        CaptcharRender.drawGraphic(120, 40, getResponse().getOutputStream(), code);
        renderNull();
    }

    public void back() {
    }

    public void no_auth() {
        setAttr("mode", getPara("mode", EnumConstant.ActionType.HTML.name()));
    }

    /**
     * TODO 退出登录
     */
    public void exit() {
        Object obj = getSession().getAttribute(SystemConstant.USER_SESSION);
        if (obj != null) {
            getSession().removeAttribute(SystemConstant.USER_SESSION);
            getSession().invalidate();
        }
        redirect("/account");
    }
}
