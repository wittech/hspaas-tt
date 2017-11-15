package com.huashi.bill.order.service;

import com.huashi.bill.order.domain.TradeOrderInvoice;

/**
 * 
 * TODO 订单发票信息
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月17日 上午12:24:28
 */
public interface ITradeOrderInvoiceService {

	/**
	 * 
	 * TODO 新增订单发票信息
	 * 
	 * @param orderId
	 *            订单ID
	 * @param addressBookId
	 *            地址薄ID
	 * @param title
	 *            发票抬头
	 * @return
	 */
	boolean save(long orderId, String title, String name, String mobile, String address);

	/**
	 * 查询订单发票
	 * 
	 * @param orderId
	 * @return
	 */
	TradeOrderInvoice getByOrderId(Long orderId);

	/**
	 * 
	 * TODO 获取用户最后一次发票信息
	 * 
	 * @param userId
	 * @return
	 */
	TradeOrderInvoice getLastest(int userId);
}
