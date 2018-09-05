package com.huashi.hsboss.web.controller.common;

import java.util.HashMap;
import java.util.Map;

import com.huashi.hsboss.constant.SystemConstant;
import com.huashi.hsboss.dto.UserSession;
import com.jfinal.core.Controller;

/**
 * 控制器基类
 * 
 * @author ym
 * @created_at 2016年6月22日下午2:11:36
 */
public class BaseController extends Controller {

	protected UserSession getUserSession() {
		return (UserSession) getSession().getAttribute(SystemConstant.USER_SESSION);
	}

	protected int getUserId() {
		return getUserSession().getUserId();
	}

	protected String getLoginName() {
		return getUserSession().getLoginName();
	}

	public void renderResultJson(boolean result, String message) {
		renderResultJson(result, message, "");
	}
	
	protected void renderResultJson(Object obj) {
		renderResultJson(true, "", obj);
	}
	
	protected void renderResultJson(boolean result, Object obj) {
		renderResultJson(result, "", obj);
	}

	protected void renderResultJson(boolean result) {
		renderResultJson(result, result ? "操作成功!" : "操作失败!");
	}

	protected void renderResultJson(boolean result, String message, Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", result);
		map.put("message", message);
		map.put("obj", object);
		renderJson(map);
	}
	
	protected int getPN() {
		return getParaToInt("pn", 1);
	}

}
