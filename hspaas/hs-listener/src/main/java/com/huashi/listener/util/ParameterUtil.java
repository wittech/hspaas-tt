package com.huashi.listener.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.listener.constant.ListenerConstant.ParameterEncoding;

/**
 * 
 * TODO 参数转换
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年11月27日 下午3:14:35
 */
public class ParameterUtil {

	private static Logger logger = LoggerFactory.getLogger(ParameterUtil.class);

	/**
	 * 
	 * TODO 转换报文数据（编码）
	 * 
	 * @param properties
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static JSONObject transform(Map<String, String[]> properties, String encoding)
			throws UnsupportedEncodingException {
		JSONObject jsonObject = new JSONObject();

		JSONObject dto = new JSONObject();

		for (String key : properties.keySet()) {
			if (properties.get(key) == null || properties.get(key).length == 0
					|| StringUtils.isEmpty(properties.get(key)[0])) {
                {
                    continue;
                }
            }

			dto.put(key, java.net.URLDecoder.decode(properties.get(key)[0], ParameterEncoding.parse(encoding)));
		}

		// 自定义参数包名称，后台exchanger 反解析， 默认字符编码UTF-8
		jsonObject.put(ParameterFilterContext.PARAMETER_NAME_IN_STREAM, JSON.toJSONString(dto));
		return jsonObject;
	}
	
	/**
	 * 
	 * TODO 转换报文数据（编码）
	 * 
	 * @param properties
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static JSONObject transformInGetMode(String qureyString, String encoding)
			throws UnsupportedEncodingException {
		if(StringUtils.isEmpty(qureyString)) {
            {
                return null;
            }
        }
		
		String queryStr = java.net.URLDecoder.decode(qureyString , ParameterEncoding.parse(encoding));
		
		JSONObject jsonObject = new JSONObject();
		
		// 自定义参数包名称，后台exchanger 反解析， 默认字符编码UTF-8
		jsonObject.put(ParameterFilterContext.PARAMETER_NAME_IN_STREAM, JSONObject.toJSONString(getParameterMapInGetMethod(queryStr)));
		
		return jsonObject;
	}

	/**
	 * 
	 * TODO 根据数据流解析内容
	 * 
	 * @param is
	 * @param contentLen
	 * @return
	 */
	public static JSONObject transform(InputStream is, int contentLen, String encoding) {
		JSONObject jsonObject = new JSONObject();
		if (contentLen > 0) {
			int readLen = 0;
			int readLengthThisTime = 0;
			byte[] message = new byte[contentLen];
			try {
				while (readLen != contentLen) {
					readLengthThisTime = is.read(message, readLen, contentLen - readLen);
					if (readLengthThisTime == -1) {// Should not happen.
						break;
					}
					readLen += readLengthThisTime;
				}

				// 自定义参数包名称，后台exchanger 反解析， 默认字符编码UTF-8
				jsonObject.put(ParameterFilterContext.PARAMETER_NAME_IN_STREAM,
						new String(message, ParameterEncoding.parse(encoding)));

			} catch (IOException e) {
				logger.error("handlerReceip error-readBytes contentLen" + contentLen, e);
			}
		}
		return jsonObject;
	}

	/**
	 * 
	 * TODO 根据数据流解析内容
	 * 
	 * @param is
	 * @param contentLen
	 * @return
	 */
	public static JSONObject transform(InputStream is, int contentLen) {
		return transform(is, contentLen, ParameterEncoding.UTF8.getEncoding());
	}

	/**
	 * 
	 * TODO 获取 GET方式HTTP location地址参数信息
	 * 
	 * @param parameterLocation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParameterMapInGetMethod(String parameterLocation) {
		if (StringUtils.isEmpty(parameterLocation)) {
            {
                return null;
            }
        }

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			String pairs[] = parameterLocation.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
                    {
                        key = param[0];
                    }
                }

				if (param.length > 1) {
                    {
                        value = param[1];
                    }
                }

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List) {
						List<String> values = (List<String>) obj;
						values.add(value);
						parameters.put(key, values);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameters;
	}
}
