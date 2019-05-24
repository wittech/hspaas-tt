package com.huashi.web.controller.mms;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.notice.vo.BaseResponse;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.mms.template.constant.MmsTemplateContext.ApproveStatus;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.web.context.HttpResponse;
import com.huashi.web.controller.BaseController;
import com.huashi.web.prervice.sms.MmsSendPrervice;

@Controller
@RequestMapping("/mms/template")
public class MmsMessageTemplateController extends BaseController {

    @Reference
    private IMmsTemplateService mmsMessageTemplateService;
    @Autowired
    private MmsSendPrervice     mmsSendPrervice;
    @Reference
    private IMmsMtSubmitService mmsMtSubmitService;

    private final Logger        logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("approveStatus", ApproveStatus.values());
        return moduleInConsole("/mms/template/template_query");
    }

    @RequestMapping(value = "/page")
    @ResponseBody
    public LayuiPage page(String page, String status, String title, Model model) {
        return parseLayuiPage(mmsMessageTemplateService.findPage(getCurrentUserId(), status, title, page));
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {
        return moduleInConsole("/mms/template/template_add");
    }

    /**
     * TODO 保存模板
     * 
     * @param content
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody BaseResponse save(String name, String title, String[] mediaTypes, String[] contents,
                                           MultipartFile[] files) {

        try {
            return mmsSendPrervice.saveModel(getCurrentUserId(), name, title, mediaTypes, contents, files);
        } catch (Exception e) {
            logger.error("保存彩信模板： userId:[" + getCurrentUserId() + "]失败", e);
            return new BaseResponse(false, "保存失败");
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, Model model) {
        model.addAttribute("template", mmsMessageTemplateService.get(id));
        return moduleInConsole("/mms/template/template_edit");
    }

    /**
     * TODO 保存模板
     * 
     * @param content
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody BaseResponse update(Long id, String name, String title, String[] mediaTypes,
                                             String[] contents, MultipartFile[] files) {
        try {
            return mmsSendPrervice.updateModel(getCurrentUserId(), name, title, mediaTypes, contents, files, id);
        } catch (Exception e) {
            logger.error("修改模板id[" + id + "] userId:[" + getCurrentUserId() + "] 失败", e);
            return new BaseResponse(false, "修改失败");
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody HttpResponse delete(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new HttpResponse(false);
        }

        return new HttpResponse(batchDelete(ids));
    }

    /**
     * TODO 批量删除
     * 
     * @param ids
     * @return
     */
    private boolean batchDelete(String ids) {
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            try {
                boolean isOk = mmsMessageTemplateService.delete(Long.valueOf(id), getCurrentUserId());
                if (!isOk) {
                    return false;
                }
            } catch (Exception e) {
                logger.error("删除失败，失败id : {}", id, e);
                return false;
            }
        }

        return true;
    }

    @RequestMapping(value = "/sendMms", method = RequestMethod.GET)
    public String sendMms(Long id, Model model) {
        model.addAttribute("template", mmsMessageTemplateService.get(id));
        return moduleInConsole("/mms/template/send_mms");
    }

    @RequestMapping(value = "/preview", method = RequestMethod.GET)
    public String preview(Long id, Model model) {
        model.addAttribute("template", mmsMessageTemplateService.getWithUserId(id, getCurrentUserId()));
        return moduleInConsole("/mms/template/preview");
    }

    @RequestMapping(value = "/previewBySid", method = RequestMethod.GET)
    public String previewBySid(Long sid, Model model) {
        model.addAttribute("template", mmsMtSubmitService.getWithUserId(sid, getCurrentUserId()));
        return moduleInConsole("/mms/template/preview");
    }
}
