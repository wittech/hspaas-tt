package com.huashi.fs.context;

import org.apache.commons.lang3.StringUtils;

/**
 * 公用枚举
 * 
 * @author zhengying
 * @create_at 2015年4月23日 下午2:24:16
 */
public class FluxContext {

	/**
	 * 
	 * TODO 账号状态
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-2 上午12:13:12
	 */
	public enum AccountStatus {
		ACTIVE(0, "启用"), FROZEN(1, "冻结"), DELETED(2, "注销");
		private int value;
		private String title;

		private AccountStatus(int value, String title) {
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
	 * TODO 产品状态
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-11 下午10:10:37
	 */
	public enum ProductStatus {
		ACTIVE(0, "可用"), FROZEN(1, "不可用");
		private int value;
		private String title;

		private ProductStatus(int value, String title) {
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
	 * TODO 运营商
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-11 下午10:28:29
	 */
	public enum TelecomCompany {
		MOBILE(1, "移动"), TELECOM(2, "电信"), UNICOM(3, "联通"), OTHER(0, "其他");
		private int value;
		private String title;

		private TelecomCompany(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public static TelecomCompany parse(String name) {
			if (StringUtils.isEmpty(name))
				return null;
			for (TelecomCompany tc : TelecomCompany.values()) {
				if (name.contains(tc.getTitle()))
					return tc;
			}
			return TelecomCompany.OTHER;
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
	 * TODO 流量类型
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-11 下午11:51:44
	 */
	public enum ProdScope {
		IN_PROVINCE(1, "省内流量"), ALL(2, "全国流量");
		private int value;
		private String title;

		private ProdScope(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public static TelecomCompany parse(String name) {
			if (StringUtils.isEmpty(name))
				return null;
			for (TelecomCompany tc : TelecomCompany.values()) {
				if (name.contains(tc.getTitle()))
					return tc;
			}
			return TelecomCompany.OTHER;
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
	 * TODO 订单充值来源
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-12 下午11:35:17
	 */
	public enum OrderType {
		WEB(0, "页面充值"), API(1, "接口接口"), ACTIVITY(2, "活动充值");
		private int value;
		private String title;

		private OrderType(int value, String title) {
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
	 * TODO 支付状态
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-12 下午11:38:00
	 */
	public enum OrderStatus {
		WAIT_CHARGE(0, "未支付"), IN_CHARED(1, "已支付");
		private int value;
		private String title;

		private OrderStatus(int value, String title) {
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
	 * TODO 交易状态
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-12 下午11:44:18
	 */
	public enum TradeStatus {
		WAIT_SEND(0, "待发送"), ORDER_SUBMIT_FAILED(1, "订单提交失败"), ORDER_SUBMITED(2,
				"待生效"), CHARGE_SUCCESS(3, "充值成功"), ORDER_CHARGE_FAIL(4, "充值失败"),
				DEALING(10, "处理中");
		private int value;
		private String title;

		private TradeStatus(int value, String title) {
			this.value = value;
			this.title = title;
		}
		
		public static TradeStatus parse(int value){
			for(TradeStatus ts : TradeStatus.values()){
				if(ts.getValue() == value)
					return ts;
			}
			return null;
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
	 * TODO 文件类型
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2015-8-19 下午10:21:15
	 */
	public enum FileType {
		TXT, XLS, XLSX
	}
	
	/**
	 * 
	  * TODO 金额变动类型
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2015-8-26 下午9:14:09
	 */
	public enum FeeType{
		
		BILL(0, "充值"), CHARGE(1, "支付"), DEDUCT(2, "扣值"), RETURN(3,"失败返还");
		private int value;
		private String title;

		private FeeType(int value, String title) {
			this.value = value;
			this.title = title;
		}
		
		public static FeeType parse(int value){
			for(FeeType ft : FeeType.values()){
				if(ft.getValue() == value)
					return ft;
			}
			return null;
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
	  * TODO 登陆状态
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2015-9-8 下午9:57:14
	 */
	public enum LoginStatus{
		SUCCESS(0, "成功"), FAILURE(1, "失败");
		private int value;
		private String title;

		private LoginStatus(int value, String title) {
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
	  * TODO 登陆类型
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2015-9-8 下午9:57:03
	 */
	public enum LoginType{
		OWNER(0, "自行用户名密码登陆"), ADMIN(1, "管理员快捷登录");
		private int value;
		private String title;

		private LoginType(int value, String title) {
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
