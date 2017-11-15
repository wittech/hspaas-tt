
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.web.controller.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.settings.domain.AddressBook;
import com.huashi.common.settings.service.IAddressBookService;
import com.huashi.common.util.DateUtil;
import com.huashi.web.controller.BaseController;

/**
 * TODO 地址管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月8日 下午7:22:47
 */
@Controller
@RequestMapping("/settings/address_book")
public class AddressBookController extends BaseController {

	@Reference
	private IAddressBookService addressBookService;

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
		return "/settings/address_book/index";
	}

	/**
	 * 添加
	 * 
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "/settings/address_book/create";
	}

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model, int id) {
		model.addAttribute("addressBook", addressBookService.getById(id));
		return "/settings/address_book/edit";
	}

	/**
	 * 数据列表
	 * 
	 * @param request
	 * @param currentPage
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(String mobile,String currentPage, String starDate, String endDate, Model model) {
		model.addAttribute("page",
				addressBookService.findPage(getCurrentUserId(), mobile,starDate, endDate, request.getParameter("currentPage")));
		return "/settings/address_book/list";
	}
	
	/**
	 * 地址列表 提供下单开发票选择使用
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/address_list")
	public String address_list(Model model){
		model.addAttribute("addressList", addressBookService.findList(getCurrentUserId()));
		return "/settings/address_book/address_list";
	}

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public @ResponseBody boolean insert(AddressBook addressBook) {
		addressBook.setUserId(getCurrentUserId());
		return addressBookService.save(addressBook);
	}

	/**
	 * 
	 * TODO修改
	 * 
	 * @param addressBook
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody boolean update(AddressBook addressBook) {
		addressBook.setUserId(getCurrentUserId());
		return addressBookService.update(addressBook);
	}

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Integer delete(int id) {
		return addressBookService.delete(id);
	}

}
