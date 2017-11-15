package com.huashi.bill.order.constant;

public class ExchangeOrderContext {

	/**
	 * 
	  * TODO 转存产品类型
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2016年9月18日 下午3:41:21
	 */
	public enum ExchangeType {
		// 产品剩余量是指 短信剩余条数，流量剩余金额等
		PLATFORM_PRODUCT_BALANCE(0, "产品剩余量"), 
		
		ACCOUNT_MONEY(1, "站内账户金额");

		private int value;
		private String title;

		private ExchangeType(int value, String title) {
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
	  * TODO 转存订单状态
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2016年9月18日 下午3:01:11
	 */
	public enum ExchangeStatus {
		
		WAIT_PROCESS(0, "待处理"), COMPLETED(1, "处理完成"), DEAL_FAILED(2, "处理失败");
		private int value;
		private String title;

		private ExchangeStatus(int value, String title) {
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
