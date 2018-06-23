//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月27日       杨猛　     新建
//*********************************************************************
package com.huashi.hsboss.web.controller.sms;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.service.ISmsMtTaskService;
import com.huashi.sms.template.context.TemplateContext.ApproveStatus;
import com.huashi.sms.template.context.TemplateContext.ModeOperation;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;
import com.jfinal.ext.route.ControllerBind;

/**
 * @author ym
 * @created_at 2016年8月27日下午8:54:48
 */
@ViewMenu(code = MenuCode.MENU_CODE_2003)
@ControllerBind(controllerKey = "/sms/message_template")
public class MessageTemplateController extends BaseController {

	@BY_NAME
	private ISmsTemplateService iSmsTemplateService;
	@BY_NAME
	private IUserService iUserService;
	@BY_NAME
	private ISmsMtTaskService iSmsMtTaskService;

	public void index() {
		String userId = getPara("userId");
		String keyword = getPara("keyword");
		String status = getPara("status");
		BossPaginationVo<MessageTemplate> page = iSmsTemplateService.findPageBoos(getPN(), keyword, status,userId);
		setAttr("keyword", keyword);
		if(StringUtils.isNotEmpty(status)){
			setAttr("status",Integer.parseInt(status));
		}
		setAttr("userId", userId);
		setAttr("username", getPara("username"));
		setAttr("page", page);
		setAttr("approveStatus", ApproveStatus.values());
	}

	public void add() {
		MessageTemplate messageTemplate = getModel(MessageTemplate.class);
		messageTemplate.setApproveUser(getLoginName());
		messageTemplate.setAppType(ModeOperation.OPERATIONSUPPORT.getValue());
		messageTemplate.setApproveTime(new Date());
		messageTemplate.setStatus(ApproveStatus.SUCCESS.getValue());
        boolean result = iSmsTemplateService.saveToBatchContent(messageTemplate,getParaMap().get("content"));
		renderResultJson(result);
	}

	public void create() {
		long sid = getParaToLong("sid",-1L);
		
		// 如果SID和子任务ID不为空则表明此次操作为（子任务中的敏感词导白操作）
		if(sid > -1){
			SmsMtTask task = iSmsMtTaskService.getTaskBySid(sid);
            setAttr("task", task);
            // 敏感词导白，词汇转义为短信模板要求格式 **|**
            setAttr("forbiddenWords", task == null || StringUtils.isBlank(task.getForbiddenWords()) ? "" 
            		: task.getForbiddenWords().replaceAll(",", "|"));
		}
		setAttr("templateStatus", ApproveStatus.values());
		setAttr("routeTypes", PassageContext.RouteType.values());
		String userId = getPara("userId");
		if(StringUtils.isNotEmpty(userId)){
			setAttr("userId",Integer.parseInt(userId));
		}
		//所有融合平台用户
		setAttr("userList", iUserService.findUserModels());
	}

	/**
	 * 
	   * TODO 编辑（跳转到修改页面）
	 */
	public void edit() {
        long id = getParaToLong("id");
        long sid = getParaToLong("sid",-1L);
        
        MessageTemplate messageTemplate = iSmsTemplateService.get(id);
        
        // 如果SID和子任务ID不为空则表明此次操作为（子任务中的敏感词导白操作）
        if(sid > -1){
        	SmsMtTask task = iSmsMtTaskService.getTaskBySid(sid);
            setAttr("task", task);
            
            if(task != null && StringUtils.isNotBlank(task.getForbiddenWords())) {
            	// 敏感词导白，词汇转义为短信模板要求格式 **|**
            	String forbiddenWords = task.getForbiddenWords().replaceAll(",", "|");
            	
            	// 如果原有模板已经有敏感词需要追加本次 导白敏感词，没有则直接加入本次导白敏感词
            	if(StringUtils.isBlank(messageTemplate.getWhiteWord()))
                 	messageTemplate.setWhiteWord(forbiddenWords);
                else
                	messageTemplate.setWhiteWord(messageTemplate.getWhiteWord() + "|" + forbiddenWords);
            }
        }
        
        setAttr("routeTypes", PassageContext.RouteType.values());
		setAttr("messageTemplate", messageTemplate);
		setAttr("userList", iUserService.findUserModels());
	}
	
	public void update() {
		MessageTemplate template = getModel(MessageTemplate.class, "messageTemplate");
		template.setApproveUser(getLoginName());
		template.setAppType(ModeOperation.OPERATIONSUPPORT.getValue());
		template.setApproveTime(new Date());
		boolean result = iSmsTemplateService.update(template);
		renderResultJson(result);
	}
	
	public void matching(){
		long id = getParaToLong("id");
		MessageTemplate template = iSmsTemplateService.get(id);
		setAttr("messageTemplate", template);
	}
	
	public void matchingSubmit(){
		long id = getParaToLong("id");
		String content = getPara("content");
		renderResultJson(iSmsTemplateService.isContentMatched(id,content));
	}
	
	public void audit(){
		setAttr("templateStatus", ApproveStatus.values());
		long id = getParaToLong("id");
		MessageTemplate template = iSmsTemplateService.get(id);
		setAttr("messageTemplate", template);
	}
	
	public void auditSubmit() {
	    MessageTemplate template = getModel(MessageTemplate.class, "messageTemplate");
        renderResultJson(iSmsTemplateService.approve(template.getId(), template.getStatus(), getLoginName()));
	}

	public void delete() {
		long id = getParaToLong("id");
		boolean result = iSmsTemplateService.deleteById(id);
		renderResultJson(result);
	}
	
	public void loadingRedis(){
	    renderResultJson(iSmsTemplateService.reloadToRedis());
	}
}
