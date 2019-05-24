
 /**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.web.controller.fs.record;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.util.DateUtil;
import com.huashi.fs.order.service.IFluxOrderService;
import com.huashi.web.controller.BaseController;

/**
  * TODO 订单管理
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年2月28日 下午9:40:53
  */
@Controller
@RequestMapping("/fs/record/order")
public class OrderController extends BaseController {
	
	@Reference
	private IFluxOrderService iorderService;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		return "/fs/record/order/index";
	}

	/**
	 * 数据列表
	 * 
	 * @param request
	 * @param currentPage
	 * @param phoneNumber
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(String currentPage, String mobile, String starDate,
			String endDate, Model model) {
		model.addAttribute("page", iorderService.findPage(getCurrentUserId(), mobile, starDate, endDate,
				request.getParameter("currentPage")));
		return "/fs/record/order/list";
	}
}
