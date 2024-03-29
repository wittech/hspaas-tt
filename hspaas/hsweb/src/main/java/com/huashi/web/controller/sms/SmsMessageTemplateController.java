package com.huashi.web.controller.sms;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.sms.template.context.TemplateContext.ApproveStatus;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;
import com.huashi.web.context.HttpResponse;
import com.huashi.web.controller.BaseController;

@Controller
@RequestMapping("/sms/template")
public class SmsMessageTemplateController extends BaseController {

    @Reference
    private ISmsTemplateService smsMessageTemplateService;

    private final Logger        logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("approveStatus", ApproveStatus.values());
        return moduleInConsole("/sms/template/template_query");
    }

    @RequestMapping(value = "/page")
    @ResponseBody
    public LayuiPage page(String page, String status, String content, Model model) {
        return parseLayuiPage(smsMessageTemplateService.findPage(getCurrentUserId(), status, content, page));
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {
        return moduleInConsole("/sms/template/template_edit");
    }

    /**
     * TODO 保存模板
     * 
     * @param content
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody HttpResponse save(String content) {
        try {
            boolean isOk = smsMessageTemplateService.save(toMessageTemplate(content, null));
            return new HttpResponse(isOk);
        } catch (Exception e) {
            logger.error("保存模板内容：[" + content + "] userId:[" + getCurrentUserId() + "]失败", e);
            return new HttpResponse(false, "保存失败");
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, Model model) {
        model.addAttribute("template", smsMessageTemplateService.get(id));
        return moduleInConsole("/sms/template/template_edit");
    }

    /**
     * TODO 保存模板
     * 
     * @param content
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody HttpResponse update(String content, Long id) {
        try {
            boolean isOk = smsMessageTemplateService.update(toMessageTemplate(content, id));
            return new HttpResponse(isOk);
        } catch (Exception e) {
            logger.error("修改模板id[" + id + "]内容：[" + content + "] userId:[" + getCurrentUserId() + "] 失败", e);
            return new HttpResponse(false, "修改失败");
        }
    }

    private MessageTemplate toMessageTemplate(String content, Long id) {
        MessageTemplate messageTemplate = null;
        if(id == null || id == 0L) {
            messageTemplate = new MessageTemplate();
            messageTemplate.setContent(content);
            messageTemplate.setUserId(getCurrentUserId());
            messageTemplate.setStatus(ApproveStatus.WAITING.getValue());
            messageTemplate.setAppType(AppType.WEB.getCode());
            messageTemplate.setCreateTime(new Date());
        } else {
            messageTemplate = smsMessageTemplateService.get(id);
            messageTemplate.setContent(content);
        }
        
        return messageTemplate;
        
    }

    /**
     * TODO 验证内容是否符合模板
     * 
     * @param id
     * @param content
     * @return
     */
    @RequestMapping(value = "/match", method = RequestMethod.POST)
    public @ResponseBody boolean match(long id, String content) {
        return smsMessageTemplateService.isContentMatched(id, content);
    }

    /**
     * 模板匹配页面
     * 
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/matching", method = RequestMethod.GET)
    public String matching(Model model, Long id) {
        model.addAttribute("msmMessageTemokate", smsMessageTemplateService.get(id));
        return "/sms/template/matching";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody HttpResponse delete(String ids) {
        if(StringUtils.isEmpty(ids)) {
            return new HttpResponse(false);
        }
        
        return new HttpResponse(batchDelete(ids));
    }
    
    /**
     * 
       * TODO 批量删除
       * @param ids
       * @return
     */
    private boolean batchDelete(String ids) {
        String[] idArray = ids.split(",");
        for(String id : idArray) {
            try {
                boolean isOk = smsMessageTemplateService.delete(Long.valueOf(id), getCurrentUserId());
                if(!isOk) {
                    return false;
                }
            } catch (Exception e) {
                logger.error("删除失败，失败id : {}", id, e);
                return false;
            }
        }
        
        return true;
    }
}
