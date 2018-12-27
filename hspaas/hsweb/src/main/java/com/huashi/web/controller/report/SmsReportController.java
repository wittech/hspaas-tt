/**
 * 
 */
package com.huashi.web.controller.report;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.report.domain.SmsSubmitHourReport;
import com.huashi.sms.report.service.ISmsSubmitHourReportService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 短信报告统计
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年12月21日 下午3:11:06
 */
@Controller
@RequestMapping("/report")
public class SmsReportController extends BaseController {

    @Reference
    private ISmsSubmitHourReportService smsSubmitHourReportService;

    /**
     * TODO 每日发送报告
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "/sms_daily", method = RequestMethod.GET)
    public String daily(Model model) {
        model.addAttribute("startDate", DateUtil.getCurrentDate());
        model.addAttribute("endDate", DateUtil.getCurrentDate());
        return moduleInConsole("/report/sms_daily");
    }

    /**
     * TODO 日报查询
     * 
     * @param page
     * @param startDate
     * @param endDate
     * @param model
     * @return
     */
    @RequestMapping(value = "/sms_daily_query")
    @ResponseBody
    public LayuiPage page(String page, String startDate, String endDate, Model model) {

        List<SmsSubmitHourReport> list = smsSubmitHourReportService.findUserSubmitReportInDailyFilter(getCurrentUserId(),
                                                                                                      startDate,
                                                                                                      endDate);
        if (CollectionUtils.isEmpty(list)) {
            return parseLayuiPage(null, "未找到相关记录", "0");
        }

        return parseLayuiPage(new PaginationVo<SmsSubmitHourReport>(list, 1, list.size()));
    }
}
