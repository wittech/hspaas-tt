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

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.bill.pay.constant.PayContext;
import com.huashi.bill.pay.exception.AlipayException;
import com.huashi.bill.pay.service.IAlipayService;
import com.huashi.bill.pay.util.ParameterParseUtil;
import com.huashi.web.controller.BaseController;
import com.huashi.web.filter.PermissionClear;

/**
 * 
 * TODO 订单支付结果
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月17日 下午7:31:21
 */
@Controller
@PermissionClear
@RequestMapping("/pay_result")
public class PayResponseController extends BaseController {

	@Reference
	private IAlipayService alipayService;

	/**
	 * 
	 * TODO 订单支付页面回执
	 * 
	 * @param provider
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/page/alipay", method = RequestMethod.GET)
	public String pageAlipay(Model model) {
		try {
			Map<String, String> params = ParameterParseUtil.parse(
					request.getParameterMap(), true);

			model.addAttribute("orderNo", request.getParameter("out_trade_no"));
			model.addAttribute("payResult",
					alipayService.isPaySuccess(params, true));
			
			alipayService.notifyUpdateOrder(params);
			
			return "/order/pay_result";

		} catch (Exception e) {
			// 返回到错误页面
			e.printStackTrace();
			model.addAttribute("error_msg", "订单处理异常");
			return "/order/pay_result";
		}
	}

	/**
	 * 
	 * TODO 支付结果异步（后台推送）
	 * 
	 * @param provider
	 */
	@RequestMapping(value = "/notify/alipay")
	@ResponseBody
	public String notify(Model model) {
		try {
			Map<String, String> params = ParameterParseUtil.parse(
					request.getParameterMap(), false);

			// 成功则发送'succsess', 表示无需再接收支付宝异步数据
			return alipayService.notifyUpdateOrder(params) ? PayContext.ALIPAY_SUCCESS_CODE
					: PayContext.ALIPAY_FAIL_CODE;

		} catch (AlipayException e) {
			// 返回到错误页面
			e.printStackTrace();
			return PayContext.ALIPAY_FAIL_CODE;
		}
	}

}
