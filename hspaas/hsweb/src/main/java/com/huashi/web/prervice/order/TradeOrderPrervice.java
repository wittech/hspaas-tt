package com.huashi.web.prervice.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.bill.order.model.AlipayTradeOrderModel;
import com.huashi.bill.order.service.ITradeOrderService;
import com.huashi.bill.product.service.IComboService;

/**
 * 
 * TODO 订单前置服务实现
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月16日 上午10:38:00
 */

@Service
public class TradeOrderPrervice {

	private Logger logger = LoggerFactory.getLogger(TradeOrderPrervice.class);

	@Reference
	private IComboService comboService;
	@Reference
	private ITradeOrderService tradeOrderService;

	@Value("${aplipay.pageUrl}")
	private String alipayPageUrl;
	@Value("${aplipay.notifyUrl}")
	private String alipayNotifyUrl;

	/**
	 * 
	 * TODO 产品套餐购买
	 * 
	 * @param userId
	 * @param comboId
	 * @param payType
	 * @param addressBookId
	 * @param invoiceTitle
	 * @param ip
	 * @return
	 */
	public String buildOrder(AlipayTradeOrderModel model) {

		model.setNotifyUrl(alipayNotifyUrl);
		model.setPageUrl(alipayPageUrl);
//		model.setTradeType(TradeType.PRODUCT_COMBO_PAY);

		try {
			return tradeOrderService.buildTradeOrder(model);
		} catch (Exception e) {
			logger.error("订单生成失败，错误信息为： {}", e.getMessage());
			return null;
		}
	}

}
