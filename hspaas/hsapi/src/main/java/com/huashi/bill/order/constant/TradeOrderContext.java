package com.huashi.bill.order.constant;

public class TradeOrderContext {

	// 针对支付宝或者微信支付，用户选择站内充值产品名称
	public static final String TRADE_ORDER_SUBJECT_TRANS_REG = "华时融合平台账户充值%s元";

	/**
	 * 
	 * TODO 交易类型
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年9月16日 上午1:27:32
	 */
	public enum TradeType {
		PRODUCT_COMBO_PAY(0, "产品套餐购买"), ACCOUNT_MONEY_CHARGE(1, "站内账户充值");

		private int value;
		private String title;

		private TradeType(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
		
		public static TradeType parse(int value){
			for(TradeType tt : TradeType.values()) {
				if(tt.getValue() == value) {
                    {
                        return tt;
                    }
                }
			}
			return TradeType.PRODUCT_COMBO_PAY;
		}
	}

	/**
	 * 
	 * TODO 订单状态
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年9月16日 上午1:27:23
	 */
	public enum TradeOrderStatus {
		WAIT_PAY(0, "待支付"), PAYED(1, "支付完成，待处理"), PAY_FAILED(2, "支付失败"), COMPLETED(
				3, "处理完成，已竣工"), DEAL_FAILED(4, "数据异常竣工");
		private int value;
		private String title;

		private TradeOrderStatus(int value, String title) {
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
