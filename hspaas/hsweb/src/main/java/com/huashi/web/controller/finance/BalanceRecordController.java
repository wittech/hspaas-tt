
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.web.controller.finance;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.finance.domain.InvoiceBalance;
import com.huashi.common.finance.domain.InvoiceRecord;
import com.huashi.common.finance.service.IInvoiceBalanceService;
import com.huashi.common.finance.service.IInvoiceRecordService;
import com.huashi.common.settings.service.IAddressBookService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 发票管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午9:11:24
 */
@Controller
@RequestMapping("/finance/balance")
public class BalanceRecordController extends BaseController {

	@Reference
	private IInvoiceRecordService balanceRecordService;
	@Reference
	private IInvoiceBalanceService invoiceBalanceService;
	@Reference
	private IAddressBookService addressBookService;

	/**
	 * 
	 * TODO 发票管理首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("invoiceBalance", invoiceBalanceService.getByUserId(getCurrentUserId()));
		return "/finance/balance/index";
	}

	/**
	 * 
	 * TODO 查询
	 * 
	 * @param request
	 * @param currentPage
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(String currentPage, Model model) {
		model.addAttribute("page", balanceRecordService.findPage(getCurrentUserId(), request.getParameter("currentPage")));
		return "/finance/balance/list";
	}

	/**
	 * 
	 * TODO 根据用户ID获取发票信息
	 * 0没有发票可开
	 * 
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public @ResponseBody boolean get() {
		boolean flag =false;
		InvoiceBalance balance = invoiceBalanceService.getByUserId(getCurrentUserId());
		if(balance !=null){
			if(balance.getMoney().intValue()>0){
				flag= true;
			}
		}
		return flag;
	}

	/**
	 * 
	 * TODO 开发票页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {
		model.addAttribute("addressList", addressBookService.findList(getCurrentUserId()));
		return "/finance/balance/create";
	}

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param balanceRecord
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody boolean save(InvoiceRecord balanceRecord) {
		balanceRecord.setUserId(getCurrentUserId());
		return balanceRecordService.save(balanceRecord);
	}

}
