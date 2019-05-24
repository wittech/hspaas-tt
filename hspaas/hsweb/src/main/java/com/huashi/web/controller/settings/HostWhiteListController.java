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

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.settings.domain.HostWhiteList;
import com.huashi.common.settings.service.IHostWhiteListService;
import com.huashi.common.util.DateUtil;
import com.huashi.web.controller.BaseController;

/**
 * TODO ip白名单
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月23日 下午10:33:48
 */
@Controller
@RequestMapping("/settings/host")
public class HostWhiteListController extends BaseController {

	@Reference
	private IHostWhiteListService hostWhiteListService;

	/**
	 * ip白名单记录首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		return "/settings/host/index";
	}

	/**
	 * ip白名单记录数据列表
	 * 
	 * @param request
	 * @param currentPage
	 * @param ip
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(String currentPage, String ip, String starDate, String endDate, Model model) {
		model.addAttribute("page", hostWhiteListService.findPage(getCurrentUserId(), ip,
				starDate, endDate, request.getParameter("currentPage")));
		return "/settings/host/list";
	}

	/**
	 * 添加ip白名单页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "/settings/host/create";
	}

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> batchInsert(HostWhiteList host) {
		host.setUserId(getCurrentUserId());
		return hostWhiteListService.batchInsert(host);
	}

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody int delete(int id) {
		return hostWhiteListService.deleteByPrimaryKey(id);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model, int id) {
		HostWhiteList hostWhiteList = hostWhiteListService.getById(id);
		model.addAttribute("hostWhiteList", hostWhiteList);
		return "/settings/host/edit";
	}

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody int update(HostWhiteList host) {
		host.setUserId(getCurrentUserId());
		return hostWhiteListService.updateByPrimaryKey(host);
	}

}
