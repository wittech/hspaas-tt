package com.huashi.developer.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.LogUtils;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.developer.response.sms.SmsSendResponse;
import com.huashi.developer.util.IpUtil;

public class BasicApiSupport {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	protected HttpServletRequest request;
	
	@Resource
	protected HttpServletResponse response;

	protected boolean validate() {

		return true;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(request.getParameterMap());
	}
	
	/**
	 * 
	   * TODO 根据Header中传递值判断调用方式
	   * 
	   * @param appType
	   * @return
	 */
	protected int getAppType() {
		String appType = request.getHeader("apptype");
		if(StringUtils.isEmpty(appType))
			return AppType.DEVELOPER.getCode();
		
		try {
			if(String.valueOf(AppType.WEB.getCode()).equals(appType))
				return AppType.WEB.getCode();
			
			if(String.valueOf(AppType.BOSS.getCode()).equals(appType))
				return AppType.BOSS.getCode();
		} catch (Exception e) {
		}
			
		return AppType.DEVELOPER.getCode();	
	}
	
	/**
	 * 
	   * TODO 返回错误码信息
	   * 
	   * @param response
	   * @param errorMessage
	 */
	protected void printErrorResponse(String errorMessage) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(JSON.toJSONString(new SmsSendResponse(JSON.parseObject(errorMessage))));
		} catch (Exception e) {
			LogUtils.error("输出回执错误信息失败", e);
		}
	}
	
	/**
	 * 
	   * TODO 获取客户端IP
	   * @return
	 */
	protected String getClientIp() {
		return IpUtil.getClientIp(request);
	}

}
