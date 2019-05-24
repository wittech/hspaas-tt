/**
 * 
 */
package com.huashi.web.controller.vs.record;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;
import com.huashi.vs.record.domain.Record;
import com.huashi.vs.record.service.IRecordService;
import com.huashi.web.controller.BaseController;

/**
 * 语音发送记录管理
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/vs/record")
public class RecordController extends BaseController {
	// 短信测试内容
	private final static String TEST_TITLE = "您的语音验证码为";
	@Reference
	private IRecordService recordService;

	/**
	 * 语言发送记录首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String success(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		return "/vs/record/index";
	}

	/**
	 * 语言发送记录加载
	 * 
	 * @param request
	 * @param currentPage
	 *            当前页码
	 * @param phoneNumber
	 *            手机号码
	 * @param starDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param status
	 *            状态
	 * @param type
	 *            类型 0语音验证码 1 语音通知
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(String currentPage, String phoneNumber, String startDate,
			String endDate, String status, Model model) {
		model.addAttribute("page", recordService.findPage(getCurrentUserId(), phoneNumber, startDate, endDate,
				request.getParameter("currentPage"), status, "0"));
		return "/vs/record/list";
	}

	/**
	 * 语音发送
	 * 
	 * @return
	 */
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public String send(Model model) {
		model.addAttribute("content", TEST_TITLE);
		model.addAttribute("security_code", RandomUtil.getRandomNum());
		return "/vs/record/send";
	}

	/**
	 * 
	 * TODO 获取语音记录对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public @ResponseBody Record get(Long id) {
		return recordService.selectByPrimaryKey(id);
	}

}
