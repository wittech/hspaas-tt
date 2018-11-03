package com.huashi.hsboss.web.controller.sms;

import java.util.Map;

import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.settings.constant.MobileBlacklistType;
import com.huashi.sms.settings.domain.SmsMobileBlackList;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.jfinal.ext.route.ControllerBind;

/**
 * TODO Author youngmeng Created 2016-10-14 15:27
 */
@ControllerBind(controllerKey = "/sms/black_list")
@ViewMenu(code = MenuCode.MENU_CODE_2004001)
public class MobileBlackListController extends BaseController {

	@Inject.BY_NAME
	private ISmsMobileBlackListService iMobileBlackListService;
	@Inject.BY_NAME
	private IUserService iUserService;

	@AuthCode(code= {OperCode.OPER_CODE_2004001001, OperCode.OPER_CODE_2004001002,OperCode.OPER_CODE_2004001003})
	@ActionMode
	public void index() {
		setAttr("page", iMobileBlackListService.findPage(getPN(), getPara("keyword")));
		setAttr("keyword", getPara("keyword"));

	}

	@AuthCode(code= OperCode.OPER_CODE_2004001002)
	@ActionMode
	public void add() {
		setAttr("types", MobileBlacklistType.values());
	}

	@AuthCode(code= OperCode.OPER_CODE_2004001002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create() {
		SmsMobileBlackList blackList = getModel(SmsMobileBlackList.class, "blackList");
		Map<String, Object> result = iMobileBlackListService.batchInsert(blackList);
		renderResultJson(result.get("result_code").toString().equals("success"), result.get("result_msg").toString());

	}

	@AuthCode(code= OperCode.OPER_CODE_2004001003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete() {
		int result = iMobileBlackListService.deleteByPrimaryKey(getParaToInt("id"));
		renderResultJson(result > 0);
	}

	public void userList() {
		String fullName = getPara("fullName");
		String mobile = getPara("mobile");
		String company = getPara("company");
		String cardNo = getPara("cardNo");
		String state = getPara("state");
		BossPaginationVo<UserProfile> page = iUserService.findPage(getPN(), fullName, mobile, company, cardNo, state,
				null);
		page.setJumpPageFunction("userJumpPage");
		setAttr("fullName", fullName);
		setAttr("mobile", mobile);
		setAttr("company", company);
		setAttr("cardNo", cardNo);
		setAttr("state", state);
		setAttr("page", page);
		setAttr("userId", getParaToInt("userId", -1));
	}

	@AuthCode(code= OperCode.OPER_CODE_2004001001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void loadingRedis() {
		renderResultJson(iMobileBlackListService.reloadToRedis());
	}

}
