/**
 * 
 */
package com.huashi.hsboss.web.controller.sms;

import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.constant.MenuCode;
import org.apache.commons.lang.StringUtils;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.context.PassageContext.PassageStatus;
import com.huashi.sms.passage.domain.SmsPassageControl;
import com.huashi.sms.passage.service.ISmsPassageControlService;
import com.huashi.sms.passage.service.ISmsPassageParameterService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 通道轮训控制管理
 * 
 * @author Administrator
 *
 */
@ControllerBind(controllerKey = "/sms/passage_control")
@ViewMenu(code= MenuCode.MENU_CODE_2001003)
public class PassageControlController extends BaseController {
	@BY_NAME
	private ISmsPassageControlService iSmsPassageControlService;
	@BY_NAME
	private ISmsPassageService iSmsPassageService;
	@BY_NAME
	private ISmsPassageParameterService iSmsPassageParameterService;

	public void index() {
		String keyword = getPara("keyword");
		String status = getPara("status");
		BossPaginationVo<SmsPassageControl> page = iSmsPassageControlService.findPage(getPN(), keyword, status);
		if (StringUtils.isNotEmpty(keyword)) {
			setAttr("keyword", Integer.parseInt(keyword));
		}
		if (StringUtils.isNotEmpty(status)) {
			setAttr("status", Integer.parseInt(status));
		}
		setAttr("page", page);
		setAttr("passageStatus", PassageStatus.values());
		setAttr("passageList", iSmsPassageService.findAll());
	}

	public void add() {
		SmsPassageControl control = getModel(SmsPassageControl.class);
		renderJson(iSmsPassageControlService.save(control));
	}

	public void create() {
		setAttr("passageStatus", PassageStatus.values());
		setAttr("passageList", iSmsPassageService.findAll());
	}

	public void edit() {
		setAttr("passageStatus", PassageStatus.values());
		setAttr("passageList", iSmsPassageService.findAll());
		long id = getParaToLong("id");
		SmsPassageControl control = iSmsPassageControlService.get((int) id);
		setAttr("smsPassageControl", control);
	}

	public void update() {
		SmsPassageControl control = getModel(SmsPassageControl.class, "smsPassageControl");
		renderJson(iSmsPassageControlService.update(control));
	}
	
	/**
	 * 更新状态 启用/停用
	 */
	public void updateStatus() {
		SmsPassageControl control = getModel(SmsPassageControl.class, "smsPassageControl");
		renderJson(iSmsPassageControlService.updateStatus(control));
	}

	public void findPassageParameter() {
		String passageId = getPara("passageId");
		if (StringUtils.isEmpty(passageId)) {
			renderResultJson(false);
		}
		renderJson(iSmsPassageParameterService.findByPassageId(Integer.parseInt(passageId)));
	}

	public void delete() {
		long id = getParaToLong("id");
		renderResultJson(iSmsPassageControlService.deleteById((int) id));
	}
}
