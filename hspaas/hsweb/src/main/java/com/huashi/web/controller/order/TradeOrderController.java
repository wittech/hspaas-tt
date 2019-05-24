
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.web.controller.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.bill.order.constant.TradeOrderContext;
import com.huashi.bill.order.constant.TradeOrderContext.TradeType;
import com.huashi.bill.order.domain.TradeOrderInvoice;
import com.huashi.bill.order.model.AlipayTradeOrderModel;
import com.huashi.bill.order.service.ITradeOrderInvoiceService;
import com.huashi.bill.order.service.ITradeOrderService;
import com.huashi.bill.pay.constant.PayContext;
import com.huashi.bill.product.service.IComboService;
import com.huashi.common.settings.service.IAddressBookService;
import com.huashi.common.util.DateUtil;
import com.huashi.web.controller.BaseController;
import com.huashi.web.prervice.order.TradeOrderPrervice;

/**
 * 
 * TODO 订单信息
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月15日 上午1:28:18
 */
@Controller
@RequestMapping("/order")
public class TradeOrderController extends BaseController {

	@Reference
	private IComboService comboService;
	@Reference
	private IAddressBookService addressBookService;
	@Autowired
	private TradeOrderPrervice tradeOrderPrervice;
	@Reference
	private ITradeOrderService tradeOrderService;
	@Reference
	private ITradeOrderInvoiceService tradeOrderInvoiceService;

	/**
	 * 
	 * TODO 产品下单选择支付方式页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index/{id}", method = RequestMethod.GET)
	public String index(@PathVariable("id") String id, Model model) {
		try {
			model.addAttribute("combo", comboService.findById(Integer.parseInt(id)));
			model.addAttribute("addresses", addressBookService.findList(getCurrentUserId()));
			//交易类型
			model.addAttribute("tradeTypeValue", TradeType.PRODUCT_COMBO_PAY.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/order/index";
	}

	/**
	 * 
	 * TODO 创建订单（产品购买或站内充值）
	 * 
	 * @param comboId
	 * @param payType
	 * @param addressBookId
	 * @param invoiceTitle
	 * @return
	 */
	@RequestMapping(value = "/build", method = RequestMethod.POST)
	public @ResponseBody String build(AlipayTradeOrderModel model) {
		
		model.setIp(getClientIp());
		model.setUserId(getCurrentUserId());
		
		return tradeOrderPrervice.buildOrder(model);
	}
	
	@RequestMapping(value = "/page_resp/alipay", method = RequestMethod.GET)
	public void alipayResult() {

	}

	/**
	 * 消费首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/consumption")
	public String consumption(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		model.addAttribute("statusValues", TradeOrderContext.TradeOrderStatus.values());
		model.addAttribute("payValues", PayContext.PayType.values());
		return "/order/consumption";
	}

	/**
	 * 消费记录查询
	 * 
	 * @param request
	 * @param currentPage
	 * @param phoneNumber
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/consumption_list")
	public String consumptionList(String currentPage, String orderNo, String starDate, String endDate, String status,
			String payType, Model model) {
		model.addAttribute("page", tradeOrderService.findPage(getCurrentUserId(), orderNo, starDate, endDate,
				currentPage, status, payType));
		return "/order/consumption_list";
	}

	/**
	 * 
	 * TODO 订单发票信息
	 * 
	 * @param comboId
	 * @param payType
	 * @param addressBookId
	 * @param invoiceTitle
	 * @return
	 */
	@RequestMapping(value = "/invoice", method = RequestMethod.POST)
	public @ResponseBody TradeOrderInvoice getInovice(Long orderId) {
		return tradeOrderInvoiceService.getByOrderId(orderId);
	}

}
