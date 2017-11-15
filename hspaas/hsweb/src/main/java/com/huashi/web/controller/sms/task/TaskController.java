/**
 * 
 */
package com.huashi.web.controller.sms.task;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.util.DateUtil;
import com.huashi.sms.task.service.ISmsMtTaskService;
import com.huashi.web.controller.BaseController;

/**
 * 任务管理
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/sms/task")
public class TaskController extends BaseController {
	@Reference
	private ISmsMtTaskService ismsMtTaskService;

	/**
	 * 任务首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("start_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		return "/sms/task/index";
	}

	/**
	 * 首页任务查询
	 * 
	 * @param currentPage
	 * @param sid
	 * @param starDate
	 * @param endDate
	 * @param content
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(String currentPage, String sid, String starDate, String endDate, String content, Model model) {
		Long start = null;
		Long end = null;
		if (StringUtils.isNotBlank(starDate)) {
			start = DateUtil.getSecondDate(starDate+" 00:00:00").getTime();
		} else {
			starDate = DateUtil.getDayStr(new Date()) + " 00:00:00";
			start = DateUtil.getSecondDate(starDate).getTime();

		}
		if (StringUtils.isNotBlank(endDate)) {
			end = DateUtil.getSecondDate(endDate+" 00:00:00").getTime();
		}
		model.addAttribute("page", ismsMtTaskService.findAll(getCurrentUserId(), sid, content, start, end,
				request.getParameter("currentPage")));
		return "/sms/task/list";
	}

}
