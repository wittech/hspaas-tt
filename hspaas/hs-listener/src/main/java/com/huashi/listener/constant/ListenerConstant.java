package com.huashi.listener.constant;

import org.apache.commons.lang3.StringUtils;

public class ListenerConstant {

	// 昨日消费记录更新表达式（每天凌晨1点钟）
	public static final String CONSUMPTION_EXECUTE_CRON = "0 0 1 * * ?";

	/**
	 * 
	  * TODO 参数编码，shortCode 为简码用于在推送的URL中定义
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2016年11月30日 下午5:00:06
	 */
	public enum ParameterEncoding {
		UTF8("u", "UTF-8"), GBK("g", "GBK"), GB2312("g2", "GB2312");
		
		private String shortCode;
		private String encoding;

		private ParameterEncoding(String shortCode, String encoding) {
			this.shortCode = shortCode;
			this.encoding = encoding;
		}

		/**
		 * 
		 * TODO 转换
		 * 
		 * @param filterCode
		 * @return
		 */
		public static String parse(String code) {
			if (StringUtils.isEmpty(code))
				return ParameterEncoding.UTF8.getEncoding();

			for (ParameterEncoding pf : ParameterEncoding.values()) {
				if (pf.getShortCode().equalsIgnoreCase(code))
					return pf.getEncoding();
			}
			return ParameterEncoding.UTF8.getEncoding();
		}

		public String getShortCode() {
			return shortCode;
		}

		public String getEncoding() {
			return encoding;
		}
		
	}
}
