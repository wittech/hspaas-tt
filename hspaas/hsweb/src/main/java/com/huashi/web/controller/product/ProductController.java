
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.web.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.bill.product.service.IComboService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 产品报价
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月17日 下午10:48:47
 */
@Controller
@RequestMapping("/product")
public class ProductController extends BaseController{

	@Reference
	private IComboService comboService;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		return "/product/index";
	}

	/**
	 * 
	 * TODO 列表
	 * 
	 * @param request
	 * @param type
	 * @param currentPage
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public String list(String type, String currentPage, Model model) {
		model.addAttribute("page", comboService.findPageList(request.getParameter("currentPage")));
		return "/product/list";
	}
	
}
