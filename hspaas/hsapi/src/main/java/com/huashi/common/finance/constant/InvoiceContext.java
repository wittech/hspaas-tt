package com.huashi.common.finance.constant;

public class InvoiceContext {

	/**
	 * 
	 * TODO 发票类型
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2016年10月12日 上午11:24:28
	 */
	public enum InvoiceType {
		GENERAL(0, "普通发票"), VALUE_ADDED_TAX_SPECIAL(2, "增值税专用发票");

		InvoiceType(int code, String title) {
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
	 * TODO 邮寄费用付款方式
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2016年10月12日 上午11:25:10
	 */
	public enum ExpressFeeType {
		FREIGHT_COLLECT(0, "到付"), CARRIAGE_PREPAID(1, "预付");

		ExpressFeeType(int code, String title) {
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
	 * TODO 包裹邮寄状态
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2016年10月12日 上午11:28:14
	 */
	public enum ExpressStatus {
		WAITING(0, "待邮寄"), POSTING(1, "已邮寄"), ALREADY_SIGN(2, "已签收"), ERROR(3, "包裹异常");

		ExpressStatus(int code, String title) {
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
	  * TODO 快递公司
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2016年10月12日 上午11:53:00
	 */
	public enum ExpressCompany {
		SF("顺丰速运", "95338", "http://www.sf-express.com"), 
			STO("申通快递", "95543", "http://www.sto.cn"), 
			YT("圆通速递", "95554", "http://www.yto.net.cn/"), 
			ZTO("中通快递", "95311", "http://www.zto.cn"), 
			BEST_EXPRESS("百世汇通", "4009 565656", "http://www.800bestex.com/"), 
			YD("韵达快递", "95546", "http://www.yundaex.com"),
			TTK("天天快递", "400-188-8888", "http://www.ttkdex.com"),
			CHINA_POST("中国邮政", "11185", "http://yjcx.chinapost.com.cn"),
			EMS("EMS邮政特快专递", "11183", "http://www.ems.com.cn/"), 
			ZJS("宅急送", "400-6789-000", "http://www.zjs.com.cn"), 
			DEPPON("德邦", "95353", "http://www.deppon.com"), 
			QF("全峰快递", "400-100-0001", "http://www.qfkd.com.cn");
		
		ExpressCompany(String title, String phone, String url) {
			this.title = title;
			this.phone = phone;
			this.url = url;
		}

		private String title;
		private String phone;
		private String url;

		public String getPhone() {
			return phone;
		}

		public String getTitle() {
			return title;
		}

		public String getUrl() {
			return url;
		}
		
		
	}

}
