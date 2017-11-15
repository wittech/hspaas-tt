package com.huashi.bill.order.service;

import com.huashi.bill.order.model.ExchangeOrderModel;

/**
 * 
 * TODO 转存余额服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月18日 下午3:24:41
 */
public interface IExchangeOrderService {

	/**
	 * 
	 * TODO 创建转存订单
	 * 
	 * @param model
	 * @return
	 */
	String buildExchangeOrder(ExchangeOrderModel model);

}
