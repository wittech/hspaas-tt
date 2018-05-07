/**
 * 
 */
package com.huashi.web.controller.sms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.notice.service.IMessageSendService;
import com.huashi.common.notice.vo.SmsResponse;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;
import com.huashi.sms.record.service.ISmsApiFaildRecordService;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.web.controller.BaseController;

/**
 * 短信管理 发送记录、错误记录、接收记录
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/sms/record")
public class SmsSendController extends BaseController {

	// 短信测试内容
	private final static String TEST_TITLE = "您的短信验证码为";

    @Reference
    private ISmsMtSubmitService submitService;
    @Reference
    private IMessageSendService messageSendService;
    @Reference
    private IUserDeveloperService userDeveloperService;
	@Reference
	private ISmsMoMessageService moMassageReceiveService;
	@Reference
	private ISmsApiFaildRecordService smsApiFailedRecordService;

	/**
	 * 短信错误记录首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/error/index", method = RequestMethod.GET)
	public String errorIndex(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		return "/sms/record/error/index";
	}

	/**
	 * 短信错误记录数据列表
	 * 
	 * @param request
	 * @param currentPage
	 * @param phoneNumber
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/error/list")
	public String errorList(String currentPage, String phoneNumber, String starDate, String endDate, Model model) {
		model.addAttribute("page", smsApiFailedRecordService.findPage(getCurrentUserId(), phoneNumber, starDate,
				endDate, request.getParameter("currentPage")));
		return "/sms/record/error/list";
	}

	/**
	 * 短信接收记录首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/receive/index", method = RequestMethod.GET)
	public String receiveIndex(Model model) {
		model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		model.addAttribute("stop_date", DateUtil.getCurrentDate());
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		return "/sms/record/receive/index";
	}

	/**
	 * 短信错误记录数据列表
	 * 
	 * @param request
	 * @param currentPage
	 * @param phoneNumber
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/receive/list")
	public String receiveList(String currentPage, String phoneNumber, String startDate, String endDate, Model model) {
		model.addAttribute("page", moMassageReceiveService.findPage(getCurrentUserId(), phoneNumber, startDate, endDate,
				request.getParameter("currentPage")));
		return "/sms/record/receive/list";
	}

	/**
	 * 短信发送记录首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/send/index", method = RequestMethod.GET)
	public String sendIndex(Model model, String sid,String startDate,String endDate) {
		if(StringUtils.isNotEmpty(startDate)){
			model.addAttribute("start_date", startDate);
		}else{
			model.addAttribute("start_date", DateUtil.getDayGoXday(-7));
		}
		if(StringUtils.isNotEmpty(endDate)){
			model.addAttribute("stop_date", endDate);
		}else{
			model.addAttribute("stop_date", DateUtil.getCurrentDate());
		}
		model.addAttribute("min_date", DateUtil.getMonthXDay(-3));
		model.addAttribute("sid", sid);
		return "/sms/record/send/index";
	}

	/**
	 * 短信发送记录数据列表
	 * 
	 * @param request
	 * @param currentPage
	 * @param phoneNumber
	 * @param starDate
	 * @param endDate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/send/list")
	public String sendList(String currentPage, String phoneNumber, String starDate, String endDate, String sid,
			Model model) {
		model.addAttribute("page", submitService.findPage(getCurrentUserId(), phoneNumber, starDate, endDate,
				request.getParameter("currentPage"), sid));
		return "/sms/record/send/list";
	}

	/**
	 * 短信发送
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/send/create", method = RequestMethod.GET)
	public String create(Model model) {
		return "/sms/record/send/create";
	}

	/**
	 * 
	 * TODO 短信发送
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "/send_sms", method = RequestMethod.POST)
	public @ResponseBody SmsResponse sendSms(String mobile, String content) {
		UserDeveloper d = userDeveloperService.getByUserId(getCurrentUserId());

		return messageSendService.sendCustomMessage(d.getAppKey(), d.getAppSecret(), mobile, content);
	}

	/**
	 * 读取文件 解析号码
	 * 
	 * @param filePath
	 */
	@RequestMapping(value = "/read_file", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> readFile(String fileName) {
//		String mobleNumbers = ExcelUtil.readExcelFirstColumn(tmpStoreDirectory + fileName);
//		if (StringUtils.isEmpty(mobleNumbers)) {
//            return null;
//        }
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("mobleNumbers", mobleNumbers);
		map.put("resultCode", "0");
		return map;
	}

	/**
	 * 
	 * TODO 短信测试 首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/test/index")
	public String testIndex(Model model) {
		model.addAttribute("content", TEST_TITLE);
		model.addAttribute("security_code", RandomUtil.getRandomNum());
		return "/sms/record/test/index";
	}

	/**
	 * 
	 * TODO 短信测试
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "/test/send", method = RequestMethod.POST)
	public @ResponseBody SmsResponse send(String mobile, String securityCode) {
		UserDeveloper d = userDeveloperService.getByUserId(getCurrentUserId());

		return messageSendService.sendCustomMessage(d.getAppKey(), d.getAppSecret(), mobile, TEST_TITLE + securityCode);
	}

	@RequestMapping(value = "/multi_send", method = RequestMethod.POST)
	@ResponseBody
	public void multiSend(MultipartFile file) {
		// byte[] fileByte;
		// try {
		// fileByte = file.getBytes();
		// } catch (IOException e) {
		// throw new RuntimeException("读取上传文件失败");
		// }
		//
		// String fileFormat;
		// try {
		// fileFormat = file.getOriginalFilename().split("\\.")[1];
		// } catch (Exception e) {
		// throw new RuntimeException("读取上传文件失败");
		// }

	}

}
