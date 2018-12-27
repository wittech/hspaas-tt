/**
 * 
 */
package com.huashi.web.controller.sms;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.notice.service.IMessageSendService;
import com.huashi.common.notice.vo.SmsResponse;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.DateUtil;
import com.huashi.sms.record.service.ISmsApiFaildRecordService;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.web.controller.BaseController;
import com.huashi.web.prervice.sms.SmsSendPrervice;

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

    @Autowired
    private SmsSendPrervice           smsSendPrervice;

    /**
     * 短信发送
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        return moduleInConsole("/sms/send_sms");
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
    @RequestMapping(value = "/read_file/{file_type}", method = RequestMethod.POST)
    public @ResponseBody JSONObject readFile(@PathVariable(value = "file_type") String fileType,
                                             MultipartFile file) {
        JSONObject response = new JSONObject();
        try {
            Set<String> mobiles = smsSendPrervice.readMobilesFromFile(fileType, file);
            response.put("mobiles", mobiles);
            response.put("count", mobiles.size());
            response.put("result", "0");

        } catch (Exception e) {
            response.put("result", "1");
        }

        return response;
    }

    /**
     * 短信发送记录首页
     * 
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(Model model) {
        model.addAttribute("startDate", DateUtil.getCurrentDate());
        model.addAttribute("endDate", DateUtil.getCurrentDate());
        return moduleInConsole("/sms/send_query");
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
    public LayuiPage page(String page, String sid, String mobile, String startDate, String endDate, Model model) {
        return parseLayuiPage(submitService.findPage(getCurrentUserId(), mobile, startDate, endDate, page, sid));
    }

}
