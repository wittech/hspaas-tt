package com.huashi.web.controller.sms.template;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.sms.template.context.TemplateContext.ApproveStatus;
import com.huashi.sms.template.context.TemplateContext.MessageTemplateType;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;
import com.huashi.web.context.HttpResponse;
import com.huashi.web.controller.BaseController;

@Controller
@RequestMapping("/sms/template")
public class MessageTemplateController extends BaseController {

	@Reference
	private ISmsTemplateService smsMessageTemplateService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("approveStatus", ApproveStatus.values());
		return "/sms/template/index";
	}

	@RequestMapping(value = "/list")
	public String list(String currentPage,
			String status, String content, Model model) {
		model.addAttribute("page", smsMessageTemplateService.findPage(getCurrentUserId(), status, content,
				request.getParameter("currentPage")));
		return "/sms/template/list";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {
		model.addAttribute("types", MessageTemplateType.values());
		return "/sms/template/create";
	}
	
	/**
	 * 
	 * TODO 保存模板
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody HttpResponse save(MessageTemplate template) {
		try {
			template.setAppType(AppType.WEB.getCode());
			template.setUserId(getCurrentUserId());
			if(smsMessageTemplateService.save(template)) {
                {
                    return new HttpResponse(true);
                }
            }
			
			return new HttpResponse(false, "操作失败");
		} catch (Exception e) {
			return new HttpResponse(false, e.getMessage());
		}
	}
	
	/**
	 * 
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
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/matching", method = RequestMethod.GET)
	public String matching(Model model,Long id) {
		model.addAttribute("msmMessageTemokate", smsMessageTemplateService.get(id));
		return "/sms/template/matching";
	}
}
