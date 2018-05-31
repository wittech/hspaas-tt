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

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.notice.service.IMessageSendService;
import com.huashi.common.notice.vo.SmsResponse;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.DateUtil;
import com.huashi.sms.record.service.ISmsApiFaildRecordService;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.web.controller.BaseController;

/**
 * 短信管理 发送记录、错误记录、接收记录
 * 
 * @author Administrator
 */
@Controller
@RequestMapping("/sms/send")
public class SmsSendController extends BaseController {

    @Reference
    private ISmsMtSubmitService       submitService;
    @Reference
    private IMessageSendService       messageSendService;
    @Reference
    private IUserDeveloperService     userDeveloperService;
    @Reference
    private ISmsMoMessageService      moMassageReceiveService;
    @Reference
    private ISmsApiFaildRecordService smsApiFailedRecordService;

    /**
     * 短信发送
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        return "/console/sms/send_sms";
    }

    /**
     * TODO 短信发送
     * 
     * @param mobile
     * @param content
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
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
        // String mobleNumbers = ExcelUtil.readExcelFirstColumn(tmpStoreDirectory + fileName);
        // if (StringUtils.isEmpty(mobleNumbers)) {
        // return null;
        // }
        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("mobleNumbers", mobleNumbers);
        map.put("resultCode", "0");
        return map;
    }

    /**
     * 短信发送记录首页
     * 
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(Model model, String sid, String startDate, String endDate) {
        if (StringUtils.isNotEmpty(startDate)) {
            model.addAttribute("startDate", startDate);
        } else {
            model.addAttribute("startDate", DateUtil.getDayGoXday(-7));
        }
        if (StringUtils.isNotEmpty(endDate)) {
            model.addAttribute("endDate", endDate);
        } else {
            model.addAttribute("endDate", DateUtil.getCurrentDate());
        }
        model.addAttribute("minDate", DateUtil.getMonthXDay(-3));
        model.addAttribute("sid", sid);
        return "/console/sms/send_query";
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
    @RequestMapping(value = "/page")
    @ResponseBody
    public LayuiPage page(String currentPage, String mobile, String starDate, String endDate, Model model) {
        return parseLayuiPage(submitService.findPage(getCurrentUserId(), mobile, starDate, endDate,currentPage, null));
    }

}
