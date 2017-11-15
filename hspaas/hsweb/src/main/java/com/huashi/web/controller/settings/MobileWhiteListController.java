
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

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.util.DateUtil;
import com.huashi.sms.settings.domain.SmsMobileWhiteList;
import com.huashi.sms.settings.service.ISmsMobileWhiteListService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 白名单管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月20日 下午1:47:43
 */

@Controller
@RequestMapping("/settings/mobile/white")
public class MobileWhiteListController extends BaseController {

	@Reference
	private ISmsMobileWhiteListService mobileWhiteService;

	/**
	 * 
	 * TODO 首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		model.addAttribute("massageWhiteList", mobileWhiteService.selectByUserId(getCurrentUserId()));
		return "/settings/mobile/white/index";
	}

	/**
	 * 
	 * TODO 设置页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {
		return "/settings/mobile/white/create";
	}

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> batchInsert(SmsMobileWhiteList white) {
		white.setUserId(getCurrentUserId());
		return mobileWhiteService.batchInsert(white);
	}

	/**
	 * 查询分页
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
	public String list(String currentPage, String phoneNumber, String starDate,
			String endDate, Model model) {
		model.addAttribute("page", mobileWhiteService.findPage(getCurrentUserId(), phoneNumber, starDate, endDate,
				request.getParameter("currentPage")));
		return "/settings/mobile/white/list";
	}

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody int update(int id) {
		return mobileWhiteService.deleteByPrimaryKey(id);
	}
}
