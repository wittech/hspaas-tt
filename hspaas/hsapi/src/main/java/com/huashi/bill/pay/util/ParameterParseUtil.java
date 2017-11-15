package com.huashi.bill.pay.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * TODO 支付宝参数转换
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月15日 上午12:33:53
 */
public class ParameterParseUtil {

	/**
	 * 
	 * TODO 转换支付宝发送回来的参数，以免出现中文乱码
	 * 
	 * @param params
	 * @param isAsync
	 * @return
	 */
	public static Map<String, String> parse(Map<String, String[]> params, boolean isAsync) {
		Map<String, String> newParam = new HashMap<String, String>();
		for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) params.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			if (!isAsync) {
				try {
					// 同步发送支付宝回执数据执行
					valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			newParam.put(name, valueStr);
		}
		return newParam;
	}
}
