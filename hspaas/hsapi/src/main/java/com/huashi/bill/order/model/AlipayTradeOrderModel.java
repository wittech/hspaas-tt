package com.huashi.bill.order.model;

public class AlipayTradeOrderModel extends TradeOrderModel {

	private static final long serialVersionUID = -292358817414980108L;
	private String pageUrl;
	private String notifyUrl;

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public boolean validate() {
//		if(PaySource.)
//		
//		if(userId == 0 || comboId == 0 || )
//			return false;
//		if(comboId == 0)
//			return false;
		
			
		return false;
	}

}
