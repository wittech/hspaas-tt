package com.huashi.hsboss.web.controller.sms;

import java.util.Map;

import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
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

	public void index() {
		setAttr("page", iMobileBlackListService.findPage(getPN(), getPara("keyword")));
		setAttr("keyword", getPara("keyword"));

	}

	public void add() {
		setAttr("types", MobileBlacklistType.values());
	}

	public void create() {
		SmsMobileBlackList blackList = getModel(SmsMobileBlackList.class, "blackList");
		Map<String, Object> result = iMobileBlackListService.batchInsert(blackList);
		renderResultJson(result.get("result_code").toString().equals("success"), result.get("result_msg").toString());

	}

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

	public void loadingRedis() {
		renderResultJson(iMobileBlackListService.reloadToRedis());
	}

}
