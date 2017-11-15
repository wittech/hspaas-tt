package com.huashi.common.user.context;

public class UserSettingsContext {

	/**
	 * 
	 * TODO 用户短信失败返还规则
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2016年9月18日 下午6:09:23
	 */
	public enum SmsReturnRule {
		NO(0, "不返回"), YES_WITH_AUTO(1, "失败自动返还"), YES_WITH_TIMEOUT(2, "超时未回执返还");
		
		private int value;
		private String title;

		private SmsReturnRule(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}

	/**
	 * 
	 * TODO 短信内容免审核规则
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年10月2日 下午5:22:57
	 */
	public enum SmsMessagePass {
		NO(0, "不需要"), YES(1, "需要");

		private int value;
		private String title;

		private SmsMessagePass(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}

	/**
	 * 
	 * TODO 是否需要报备模板
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年10月2日 下午8:04:34
	 */
	public enum SmsNeedTemplate {
		NO(0, "不需要"), YES(1, "需要");

		private int value;
		private String title;

		private SmsNeedTemplate(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}

	/**
	 * 
	 * TODO 签名途径
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年10月2日 下午10:31:05
	 */
	public enum SmsSignatureSource {
		SELF_MANAGE(0, "自维护"), HSPAAS_AUTO_APPEND(1, "系统强制");
		private int value;
		private String title;

		private SmsSignatureSource(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}
	
	/**
	 * 
	  * TODO 短信是否自动提取模板
	  *
	  * @author zhengying
	  * @version V1.0.0   
	  * @date 2016年11月12日 下午2:02:00
	 */
	public enum SmsPickupTemplate {
		NO(0, "不提取"), YES(1, "提取");
		private int value;
		private String title;

		private SmsPickupTemplate(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}

}
