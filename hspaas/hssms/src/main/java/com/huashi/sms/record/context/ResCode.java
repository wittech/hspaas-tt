/**
 * 
 */
package com.huashi.sms.record.context;

import com.huashi.sms.record.context.ResCode;

/**
 * @author Administrator
 *
 */
public class ResCode {
	public static final String SUCCESS = "200"; // 调用成功
	public static final String PARAMETERS_NOT_MATCH = "1001"; // 接口参数不匹配
	public static final String APP_AUTHENTICATION_ERROR = "1002"; // 用户认证失败
	public static final String REQUEST_IP_INVALID = "1003"; // IP或域名认证失败（非白名单用户）
	public static final String MOBILE_NOT_AVAIABLE = "1004"; // 手机号码不合法
	public static final String FLOW_DATA_PACKAGE_NOT_EXISTS = "1005"; // 流量包不存在或已失效
	public static final String FLOW_DATA_NOT_SUPPORT_LOCAL = "1006"; // 流量包不支持该归属地
	public static final String CUSTOMER_DISCOUNT_INVALID = "1007"; // 用户折扣无效
	public static final String BALANCE_NOT_ENOUGH = "1008"; // 余额不足
	public static final String CUSTOMER_OLD_PASSWORD_ERROR = "1009"; // 用户原密码错误
	public static final String ORDER_IS_NOT_EXISTS = "1010"; // 订单不存在
	public static final String SERVER_ERROR = "500"; // 系统错误

	public static String error(String code) {
		return String.format("{res_code : %s}", code);
	}

	/**
	 * 
	 * TODO 短信错误码定义
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2016-1-13 下午11:30:36
	 */
	public enum SmsCode {

		SUCCESS(0, "发送成功"), APP_AUTHENTICATION_ERROR(1, "用户鉴权错误"), REQUEST_IP_INVALID(2, "IP鉴权错误"), MOBILE_IN_BLACK(3,
				"手机号码在黑名单"), MOBILE_NOT_AVAIABLE(4, "手机号码格式错误"), CONTENT_ERROR(5, "短信内容有误"), MOBILE_NUM_BEYOND(7,
						"手机号数量超限"), USER_REVOKED(8, "账户已停用"), SERVER_ERROR(9, "未知错误"), TIMESTAMPER_EXPIRED(10,
								"时间戳已过期"), BLACK_WORDS(13, "内容包含敏感字"), BALANCE_NOT_ENOUGH(99, "账户余额不足");

		private int code;
		private String title;

		SmsCode(int code, String title) {
			this.code = code;
			this.title = title;
		}

		public int getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

		public static String parse(int code) {
			for (SmsCode sc : SmsCode.values()) {
				if (sc.getCode() == code)
					return sc.getTitle();
			}
			return SmsCode.SERVER_ERROR.getTitle();
		}

	}
}
