package com.huashi.listener.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.constant.ParameterFilterContext.ParameterFilter;
import com.huashi.listener.util.ParameterUtil;

public class BaseController {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	protected HttpServletRequest request;
	
	// 返回成功标识（目前没有具体意义）
	protected static final String SUCCESS = "1";

	protected boolean validate() {

		return true;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(request.getParameterMap());
	}

	/**
	 *  TODO 解析参数
	 *
	 * @param filterCode
	 * @param encoding
	 * @return
	 */
	protected JSONObject doTranslateParameter(Integer filterCode, String encoding) {
		ParameterFilter filter = ParameterFilter.parse(filterCode);
		JSONObject jsonObject = new JSONObject();
		try {
			if(ParameterFilter.PARAMETER_STRAEM == filter) {
				jsonObject = ParameterUtil.transform(request.getInputStream(), request.getContentLength(), encoding);
			} else {
				if(HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())){
					jsonObject = ParameterUtil.transformInGetMode(request.getQueryString(), encoding);
				}else{
					jsonObject = ParameterUtil.transform(request.getParameterMap(), encoding);
				}
			}
		} catch (Exception e) {
			logger.error("解析回执参数失败", e);
		}
		
		jsonObject.put(ParameterFilterContext.FILTER_CODE_NODE, filterCode);
		return jsonObject;
	}

	
	
}
