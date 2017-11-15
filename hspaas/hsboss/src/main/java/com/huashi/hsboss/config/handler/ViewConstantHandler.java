package com.huashi.hsboss.config.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;


/**
 * 实现视图层面需要调用的相关常量
 * @author ym
 * @created_at 2016年6月24日下午2:33:33
 */
public class ViewConstantHandler extends Handler{

	
	private Map<String, Object> constantMap = new HashMap<String,Object>();
	
	public ViewConstantHandler(Map<String, Object> constantMap){
		if(constantMap == null || constantMap.isEmpty()){
			throw new IllegalArgumentException("ViewConstantHandler init constantMap is null...");
		}else{
			this.constantMap.putAll(constantMap);
		}
	}
	
	
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		for(Map.Entry<String, Object> map : constantMap.entrySet()){
			request.setAttribute(map.getKey(), map.getValue());
		}
		nextHandler.handle(target, request, response, isHandled);
//		next.handle(target, request, response, isHandled);
	}


	public Map<String, Object> getConstantMap() {
		return constantMap;
	}


	public void setConstantMap(Map<String, Object> constantMap) {
		this.constantMap = constantMap;
	}

	
	
}
