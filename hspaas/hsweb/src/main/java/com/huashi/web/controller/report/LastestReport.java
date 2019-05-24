package com.huashi.web.controller.report;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.bill.bill.service.IBillService;
import com.huashi.web.context.WebConstants;
import com.huashi.web.controller.BaseController;

@Controller
@RequestMapping("/report/lastest")
public class LastestReport extends BaseController {

	@Reference
	private IBillService billService;

	@RequestMapping(value = "/time_chart", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> preservation(int type) {
		return billService.getConsumptionReport(getCurrentUserId(), type, WebConstants.TIME_CHART_REPORT_SIZE);
	}
}
