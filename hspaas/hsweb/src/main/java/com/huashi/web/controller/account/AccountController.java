/**
 * 
 */
package com.huashi.web.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.bill.order.constant.TradeOrderContext.TradeType;
import com.huashi.bill.order.service.ITradeOrderService;
import com.huashi.bill.product.service.IComboService;
import com.huashi.common.user.service.IUserAccountService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.web.controller.BaseController;

/**
 * 账户服务
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {
	
	@Reference
	private IUserAccountService userAccountService;
	@Reference
	private IUserBalanceService userBalanceService;
	@Reference
	private IComboService comboService;
	@Reference
	private ITradeOrderService tradeOrderService;
	
	/**
	 * 
	 * TODO 账户信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/information", method = RequestMethod.GET)
	public String information(Model model) {
		//账户总余额
		model.addAttribute("userAccount", userAccountService.getByUserId(getCurrentUserId()));
		//三种产品余额
		model.addAttribute("userbalance", userBalanceService.findByUserId(getCurrentUserId()));
		//套餐
		model.addAttribute("comboList", comboService.findLatestCombo(3));
		return "/account/information";
	}
	
	/**
	 * 
	 * TODO 充值账单
	 * 
	 * @return
	 */
	@RequestMapping(value = "/consumption", method = RequestMethod.GET)
	public String consumption(Model model) {
		return "/account/consumption";
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
		return "/account/consumption_list";
	}
	
	/**
	 * 
	 * TODO 账户充值
	 * 
	 * @return
	 */
	@RequestMapping(value = "/recharge", method = RequestMethod.GET)
	public String recharge(Model model) {
		//交易类型
		model.addAttribute("tradeTypeValue", TradeType.ACCOUNT_MONEY_CHARGE.getValue());
		return "/account/recharge";
	}
	
}
