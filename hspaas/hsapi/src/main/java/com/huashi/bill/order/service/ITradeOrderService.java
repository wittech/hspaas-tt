package com.huashi.bill.order.service;

import com.huashi.bill.order.domain.TradeOrder;
import com.huashi.bill.order.model.AlipayTradeOrderModel;
import com.huashi.bill.order.model.OfflineTradeOrderModel;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * 
 * TODO 产品套餐交易订单接口
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月16日 上午1:46:10
 */
public interface ITradeOrderService {

	/**
	 * 
	   * TODO 支付宝订单
	   * 
	   * @param model
	   * @return
	 */
	String buildTradeOrder(AlipayTradeOrderModel model);
	
	/**
	 * 
	   * TODO 线下转账订单
	   * 
	   * @param model
	   * @return
	 */
	String buildTradeOrder(OfflineTradeOrderModel model);
	

	/**
	 * 
	 * TODO 支撑系统用户录入充值
	 * 
	 * @param userId
	 *            用户编号
	 * @param productName
	 *            产品名称
	 * @param totalFee
	 *            交易总金额
	 * @param payType
	 *            支付类型，参考枚举PayContext#PayType
	 * @param operator
	 *            操作人账号，针对支付类型为支撑系统录入方式需传递此参数
	 * @return
	 */
	long buildTradeOrder(int userId, String productName, double totalFee, int payType, String operator);
	
	/**
	 * 
	   * TODO 请在此处添加注释
	   * @param tradeNo
	   * @return
	 */
	boolean updateOrderPayCompleted(String tradeNo);

	PaginationVo<TradeOrder> findPage(int userId, String orderNo,String startDate, String endDate, String currentPage,String status,String payType);

	BossPaginationVo<TradeOrder> findPage(int pageNum,String keyword,String start,String end,int status);
}
