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

import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 通道访问管理
 * @author Administrator
 *
 */
@ControllerBind(controllerKey = "/sms/passage_access")
@ViewMenu(code= MenuCode.MENU_CODE_2003004)
public class PassageAccessController extends BaseController {
	@BY_NAME
	private ISmsPassageAccessService iSmsPassageAccessService;
	@BY_NAME
	private ISmsPassageService iSmsPassageService;
	@BY_NAME
	private IUserService iUserService;
	
	@AuthCode(code= {OperCode.OPER_CODE_2003004001, OperCode.OPER_CODE_2003004002})
	@ActionMode
	public void index() {
		String keyword = getPara("keyword");
		String userId = getPara("userId");
		int user=0;
		if(StringUtils.isNotEmpty(userId)){
			user=Integer.parseInt(userId);
		}
		BossPaginationVo<SmsPassageAccess> page = iSmsPassageAccessService.findPage(getPN(), keyword, user);
		if (StringUtils.isNotEmpty(keyword)) {
			setAttr("keyword", Integer.parseInt(keyword));
		}
		setAttr("userList", iUserService.findAll());
		setAttr("userId", user);
		setAttr("page", page);
		setAttr("passageList", iSmsPassageService.findAll());
	}
	
	@AuthCode(code= OperCode.OPER_CODE_2003004002)
	@ActionMode
	public void edit() {
		setAttr("passageList", iSmsPassageService.findAccessPassages(getParaToInt("groupId"), getParaToInt("cmcp"), getParaToInt("routeType")));
		long id = getParaToLong("id");
		SmsPassageAccess access = iSmsPassageAccessService.get((int) id);
		setAttr("smsPassageAccess", access);
	}
	
	@AuthCode(code= OperCode.OPER_CODE_2003004002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		SmsPassageAccess access = getModel(SmsPassageAccess.class, "smsPassageAccess");
		renderJson(iSmsPassageAccessService.update(access));
	}
	
	@AuthCode(code= OperCode.OPER_CODE_2003004001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void loadingRedis(){
	    renderResultJson(iSmsPassageAccessService.reload());
	}
}
