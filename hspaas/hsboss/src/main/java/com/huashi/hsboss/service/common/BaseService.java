package com.huashi.hsboss.service.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 基类
 * @author ym
 * @created_at 2016年6月23日上午10:24:27
 */
public class BaseService {
	
	protected int pageSize = 20;

	protected Map<String, Object> resultMap(boolean result,String message,Object obj){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("result", result);
		map.put("message", message);
		map.put("obj", obj);
		return map;
	}
	
	protected Map<String, Object> resultMap(boolean result,String message){
		return resultMap(result,message,"");
	}
	
	protected Map<String, Object> resultSuccess(String message){
		return resultMap(true, message);
	}
	
	protected Map<String, Object> resultFail(String message){
		return resultMap(false, message);
	}
	
	protected Map<String, Object> resultDefaultSuccess(){
		return resultSuccess("操作成功！");
	}
	
	protected Map<String, Object> resultDefaultFail(){
		return resultFail("操作失败！");
	}
}
