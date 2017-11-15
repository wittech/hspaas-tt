package com.huashi.hsboss.web.controller.sms;

import java.util.HashMap;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.domain.SmsPassageMessageTemplate;
import com.huashi.sms.passage.service.ISmsPassageMessageTemplateService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 
  * TODO 通道短信内容模板管理
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年9月2日 下午5:28:27
 */
@ViewMenu(code = MenuCode.MENU_CODE_2001005)
@ControllerBind(controllerKey = "/sms/passage_message_template")
public class PassageMessageTemplateController extends BaseController {

	@BY_NAME
	private ISmsPassageMessageTemplateService iSmsPassageMessageTemplateService;
	@BY_NAME
	private ISmsPassageService iSmsPassageService;

	public void index() {
		Map<String, Object> params = appendQueryParams();
		
		BossPaginationVo<SmsPassageMessageTemplate> page = iSmsPassageMessageTemplateService.findPage(params);
		setAttr("page", page);
		setAttr("passageList", iSmsPassageService.findAll());
	}
	
	private Map<String, Object> appendQueryParams() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("content", getPara("content"));
		paramMap.put("passageId", getParaToInt("passageId"));
		paramMap.put("templateId", getPara("templateId"));
		
		paramMap.put("currentPage", getPN());
		return paramMap;
	}

	public void create() {
		setAttr("passageList", iSmsPassageService.findAll());
	}

	/**
	 * 
	   * TODO 保存
	 */
	public void save() {
		setAttr("passageList", iSmsPassageService.findAll());renderResultJson(iSmsPassageMessageTemplateService.save(getModel(SmsPassageMessageTemplate.class)));
	}

	/**
	 * 
	   * TODO 编辑（跳转到修改页面）
	 */
	public void edit() {
		setAttr("smsPassageMessageTemplate", iSmsPassageMessageTemplateService.get(getParaToLong("id")));
		setAttr("passageList", iSmsPassageService.findAll());
	}
	
	public void update() {
		renderResultJson(iSmsPassageMessageTemplateService.update(getModel(SmsPassageMessageTemplate.class)));
	}
	
	public void matching(){
		setAttr("smsPassageMessageTemplate", iSmsPassageMessageTemplateService.get(getParaToLong("id")));
	}
	
	public void matchingSubmit(){
		renderResultJson(iSmsPassageMessageTemplateService.isContentMatched(getParaToLong("id"), getPara("content")));
	}
	
	public void delete() {
		renderResultJson(iSmsPassageMessageTemplateService.delete(getParaToLong("id")));
	}
	
	public void loadingRedis(){
	    renderResultJson(iSmsPassageMessageTemplateService.reloadToRedis());
	}
}
