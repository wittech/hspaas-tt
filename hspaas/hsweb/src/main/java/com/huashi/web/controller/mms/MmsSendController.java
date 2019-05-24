/**
 * 
 */
package com.huashi.web.controller.mms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.notice.vo.BaseResponse;
import com.huashi.common.util.DateUtil;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.web.controller.BaseController;
import com.huashi.web.prervice.sms.MmsSendPrervice;

/**
 * 彩信管理 发送记录、错误记录、接收记录
 * 
 * @author Administrator
 */
@Controller
@RequestMapping("/mms/send")
public class MmsSendController extends BaseController {

    @Reference
    private IMmsMtSubmitService submitService;
    @Autowired
    private MmsSendPrervice     mmsSendPrervice;

    /**
     * 彩信发送
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        return moduleInConsole("/mms/send_mms");
    }

    /**
     * TODO 彩信发送
     * 
     * @param mobile
     * @param content
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public @ResponseBody BaseResponse sendMms(String mobile, String title, String[] mediaTypes,
                                              String[] contents, MultipartFile[] files) {
        return mmsSendPrervice.sendMms(getCurrentUserId(), mobile, title, mediaTypes, contents, files);
    }

    /**
     * TODO 彩信模板发送
     * 
     * @param mobile
     * @param content
     * @return
     */
    @RequestMapping(value = "/byModel", method = RequestMethod.POST)
    public @ResponseBody BaseResponse sendMmsByModel(String mobile, String modelId) {
        return mmsSendPrervice.sendMmsByModel(getCurrentUserId(), mobile, modelId);
    }

    /**
     * 彩信发送记录首页
     * 
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(Model model) {
        model.addAttribute("startDate", DateUtil.getCurrentDate());
        model.addAttribute("endDate", DateUtil.getCurrentDate());
        return moduleInConsole("/mms/send_query");
    }

    /**
     * 彩信发送记录数据列表
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
