package com.huashi.sms.settings.constant;

import org.apache.commons.lang3.StringUtils;

public class SmsSettingsContext {

	/**
	 * 
	 * TODO 短信敏感词开关
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2016年9月30日 下午2:35:33
	 */
	public enum ForbiddenWordsSwitch {
		OPEN("0", "开启"), CLOSE("1", "关闭");
		private String value;
		private String title;

		private ForbiddenWordsSwitch(String value, String title) {
			this.value = value;
			this.title = title;
		}

		public static ForbiddenWordsSwitch parse(String value) {
			if (StringUtils.isEmpty(value)) {
                {
                    return null;
                }
            }
			
			for (ForbiddenWordsSwitch as : ForbiddenWordsSwitch.values()) {
				if (value.equals(as.getValue())) {
                    {
                        return as;
                    }
                }
			}
			return null;
		}

		/**
		 * 
		 * TODO 判断敏感词开关是否打开（默认情况均为打开）
		 * 
		 * @param value
		 * @return
		 */
		public static boolean isOpen(String value) {
			if (StringUtils.isEmpty(value)) {
                {
                    return true;
                }
            }

			try {
				ForbiddenWordsSwitch fws = parse(value);
				if (fws == null || ForbiddenWordsSwitch.OPEN == fws) {
                    {
                        return true;
                    }
                }

				return false;
			} catch (Exception e) {
				// 数据转换异常
				return true;
			}

		}

		public String getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}
}
