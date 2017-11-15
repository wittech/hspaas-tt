package com.huashi.bill.order.dao;

import com.huashi.bill.order.domain.TradeOrderInvoice;

public interface TradeOrderInvoiceMapper {
	int deleteByPrimaryKey(Long id);

	int insert(TradeOrderInvoice record);

	int insertSelective(TradeOrderInvoice record);

	TradeOrderInvoice selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TradeOrderInvoice record);

	int updateByPrimaryKey(TradeOrderInvoice record);

	/**
	 * 
	 * TODO 根据订单ID查询订单发票信息
	 * 
	 * @param orderId
	 * @return
	 */
	TradeOrderInvoice selectByOrderId(Long orderId);

	/**
	 * 
	 * TODO 根据用户ID查询最后一次订单发票信息（支付成功订单）
	 * 
	 * @param userId
	 * @return
	 */
	TradeOrderInvoice selectLastest(int userId);

}