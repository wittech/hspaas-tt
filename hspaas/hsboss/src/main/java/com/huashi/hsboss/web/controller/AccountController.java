package com.huashi.hsboss.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.SystemConstant;
import com.huashi.hsboss.dto.UserMenu;
import com.huashi.hsboss.dto.UserSession;
import com.huashi.hsboss.model.boss.BossUser;
import com.huashi.hsboss.service.boss.BossMenuService;
import com.huashi.hsboss.service.boss.BossUserService;
import com.huashi.hsboss.util.CaptcharRender;
import com.huashi.hsboss.util.IpUtils;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 系统帐户登录、登出
 * @author ym
 * @created_at 2016年6月28日下午6:00:29
 */
@ControllerBind(controllerKey="/account")
public class AccountController extends BaseController {

	
	@BY_NAME
	private BossUserService bossUserService;
	@BY_NAME
	private BossMenuService bossMenuService;
	
	public void index(){
	}
	
	public void login(){
		
		String authCode = getPara("authCode");
		String sessionCode = getSessionAttr(SystemConstant.SESSION_VALIDATE_CODE);
		if(!authCode.toUpperCase().equals(sessionCode)){
			renderResultJson(false, "验证码输入不正确","authCode");
			return;
		}
		
		String loginName = getPara("loginName");
		String password = getPara("password");
		
		BossUser bossUser = bossUserService.findByLogin(loginName);
		if(bossUser == null){
			renderResultJson(false, "用户不存在！","loginName");
			return;
		}
		
		String md5pwd = DigestUtils.md5Hex(password);
		if(!md5pwd.toUpperCase().equals(bossUser.getStr("password").toUpperCase())){
			renderResultJson(false, "用户名或密码不正确！","password");
			return;
		}
		
		if(bossUser.getInt("disabled_flag") == 1){
			renderResultJson(false, "该用户已被禁用！","loginName");
			return;
		}
		
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
		if(userSession.isSuperAdmin()) {
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
		renderResultJson(true, "登录成功，正在跳转...","");
	}
	
	
	public void validate_code() throws IOException{
		String code = CaptcharRender.generateVerifyCode(4);
		setSessionAttr(SystemConstant.SESSION_VALIDATE_CODE, code);
		CaptcharRender.drawGraphic(120, 40, getResponse().getOutputStream(), code);
		renderNull();
	}
	
	public void back(){
		
	}

	public void no_auth() {
		setAttr("mode",getPara("mode", EnumConstant.ActionType.HTML.name()));
	}
	
	public void exit(){
		Object obj = getSession().getAttribute(SystemConstant.USER_SESSION);
		if(obj != null){
			getSession().removeAttribute(SystemConstant.USER_SESSION);
			getSession().invalidate();
		}
		redirect("/account");
	}
}
