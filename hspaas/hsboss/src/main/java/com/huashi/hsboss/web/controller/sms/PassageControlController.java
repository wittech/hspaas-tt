/**
 * 
 */
package com.huashi.hsboss.web.controller.sms;

import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;

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
@ViewMenu(code= MenuCode.MENU_CODE_2003005)
public class PassageControlController extends BaseController {
	@BY_NAME
	private ISmsPassageControlService iSmsPassageControlService;
	@BY_NAME
	private ISmsPassageService iSmsPassageService;
	@BY_NAME
	private ISmsPassageParameterService iSmsPassageParameterService;

	@AuthCode(code= {OperCode.OPER_CODE_2003005001, OperCode.OPER_CODE_2003005002,OperCode.OPER_CODE_2003005003
			,OperCode.OPER_CODE_2003005004})
	@ActionMode
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

	@AuthCode(code= OperCode.OPER_CODE_2003005001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void add() {
		SmsPassageControl control = getModel(SmsPassageControl.class);
		renderJson(iSmsPassageControlService.save(control));
	}

	@AuthCode(code= OperCode.OPER_CODE_2003005001)
	@ActionMode
	public void create() {
		setAttr("passageStatus", PassageStatus.values());
		setAttr("passageList", iSmsPassageService.findAll());
	}

	@AuthCode(code= OperCode.OPER_CODE_2003005002)
	@ActionMode
	public void edit() {
		setAttr("passageStatus", PassageStatus.values());
		setAttr("passageList", iSmsPassageService.findAll());
		long id = getParaToLong("id");
		SmsPassageControl control = iSmsPassageControlService.get((int) id);
		setAttr("smsPassageControl", control);
	}

	@AuthCode(code= OperCode.OPER_CODE_2003005002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update() {
		SmsPassageControl control = getModel(SmsPassageControl.class, "smsPassageControl");
		renderJson(iSmsPassageControlService.update(control));
	}
	
	/**
	 * 更新状态 启用/停用
	 */
	@AuthCode(code= OperCode.OPER_CODE_2003005003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
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

	@AuthCode(code= OperCode.OPER_CODE_2003005004)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete() {
		long id = getParaToLong("id");
		renderResultJson(iSmsPassageControlService.deleteById((int) id));
	}
}
