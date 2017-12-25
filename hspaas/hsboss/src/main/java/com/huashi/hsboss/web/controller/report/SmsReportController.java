package com.huashi.hsboss.web.controller.report;

import org.apache.commons.lang3.StringUtils;

import com.huashi.common.util.DateUtil;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.report.service.ISmsSubmitHourReportService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 
  * TODO 短信报告统计
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年8月19日 下午11:45:14
 */
@ControllerBind(controllerKey = "/report/sms")
@ViewMenu(code = { MenuCode.MENU_CODE_9001001, MenuCode.MENU_CODE_9001002, MenuCode.MENU_CODE_9001003, MenuCode.MENU_CODE_9001004})
public class SmsReportController extends BaseController {

	@Inject.BY_NAME
	private ISmsSubmitHourReportService iSmsSubmitHourReportService;
	@Inject.BY_NAME
	private ISmsPassageService iSmsPassageService;

	/**
	 * 
	   * TODO 用户通道统计
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_9001001)
	public void user_passage_send_report() {
		String startDate = getPara("startDate");
		if(StringUtils.isEmpty(startDate))
			startDate = DateUtil.getCurrentDate();
		
		String endDate = getPara("endDate");
		
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("username", getPara("username"));
		setAttr("userId", getParaToInt("userId", null));
		
		setAttr("container", iSmsSubmitHourReportService.findUserPassageSubmitReport(getParaToInt("userId", null), startDate, endDate));
	}
	
	/**
	 * 
	   * TODO 用户统计数据
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_9001002)
	public void user_send_report() {
		String startDate = getPara("startDate");
		if(StringUtils.isEmpty(startDate))
			startDate = DateUtil.getCurrentDate();
		
		String endDate = getPara("endDate");
		
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("username", getPara("username"));
		setAttr("userId", getParaToInt("userId", null));
		
		setAttr("container", iSmsSubmitHourReportService.findUserSubmitReport(getParaToInt("userId", null), startDate, endDate));
	}
	
	/**
	 * 
	   * TODO 通道统计数据
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_9001003)
	public void passage_send_report() {
		String startDate = getPara("startDate");
		if(StringUtils.isEmpty(startDate))
			startDate = DateUtil.getCurrentDate();
		
		String endDate = getPara("endDate");
		
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("passageId", getParaToInt("passageId", null));
		setAttr("passageList", iSmsPassageService.findAll());
		
		setAttr("container", iSmsSubmitHourReportService.findPassageSubmitReport(getParaToInt("passageId", null), startDate, endDate));
	}
	
	/**
	 * 
	   * TODO 通道统计数据
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_9001004)
	public void province_send_report() {
		String startDate = getPara("startDate");
		if(StringUtils.isEmpty(startDate))
			startDate = DateUtil.getCurrentDate();
		
		String endDate = getPara("endDate");
		
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("passageId", getParaToInt("passageId", null));
		setAttr("passageList", iSmsPassageService.findAll());
		
		setAttr("container", iSmsSubmitHourReportService.findProvinceSubmitReport(getParaToInt("passageId", null), startDate, endDate));
	}
	
	/**
	 * 
	   * TODO 获取昨日全国运营商发送数据
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_9001005)
	public void province_cmcp_report() {
		renderResultJson(iSmsSubmitHourReportService.getCmcpSubmitReport(DateUtil.getDayGoXday(-1), 
				DateUtil.getDayGoXday(-1)));
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtil.getDayGoXday(-1));
	}

}
