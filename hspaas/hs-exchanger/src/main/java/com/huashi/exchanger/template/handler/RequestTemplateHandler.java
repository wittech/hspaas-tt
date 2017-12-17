package com.huashi.exchanger.template.handler;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.huashi.exchanger.exception.DataEmptyException;
import com.huashi.exchanger.exception.DataParseException;
import com.huashi.exchanger.template.vo.TParameter;

/**
 * 
  * TODO 参数请求解析器
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年9月28日 下午2:56:23
 */
public class RequestTemplateHandler {

	public static TParameter parse(String parameter) {
		
		validate(parameter);
		
		try {
			return JSON.parseObject(parameter, TParameter.class);
		} catch (Exception e) {
			throw new DataParseException(e);
		}
	}

	private static void validate(String parameter) {
		if (StringUtils.isEmpty(parameter)) {
            throw new DataEmptyException("参数数据为空");
        }
	}
		
}
