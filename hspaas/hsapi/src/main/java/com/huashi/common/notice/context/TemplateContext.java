package com.huashi.common.notice.context;

public class TemplateContext {

	/**
	 * 
	 * TODO 短信模板代码
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年1月10日 下午3:43:19
	 */
	public enum SmsCode {
		VERIFY_CODE(1, "短信验证码"), CUSTOM_SMS_TEMPLATE_APPROVED(2, "用户自定义短信模板审批结果");

		SmsCode(int code, String title) {
			this.code = code;
			this.title = title;
		}

		private int code;
		private String title;

		public int getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

	}

	/**
	 * 
	 * TODO 邮件模板代码
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年1月10日 下午4:32:54
	 */
	public enum EmailCode {

		REGISTER_VERIFY(1, "注册邮箱校验"), REGISTER_SUCCESS(2, "注册成功"), FORGET_PASSWORD(3, "忘记密码");

		EmailCode(int code, String title) {
			this.code = code;
			this.title = title;
		}

		private int code;
		private String title;

		public int getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

	}
}
