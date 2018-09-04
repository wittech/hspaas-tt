package com.huashi.hsboss.web.controller.base;

import com.huashi.bill.pay.constant.PayContext.PaySource;
import com.huashi.bill.pay.constant.PayContext.PayType;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.domain.UserBalanceLog;
import com.huashi.common.user.service.IUserBalanceLogService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 用户账户余额管理
 * 
 * @author Administrator
 *
 */

@ControllerBind(controllerKey = "/base/user_balance")
@ViewMenu(code = MenuCode.MENU_CODE_7002)
public class UserBalanceController extends BaseController {

	@Inject.BY_NAME
	private IUserBalanceService iUserBalanceService;
	@Inject.BY_NAME
	private IUserBalanceLogService iUserBalanceLogService;
	@Inject.BY_NAME
	private IUserService iUserService;

	public void index() {
		Integer userId = getParaToInt("userId", 0);
		BossPaginationVo<UserBalance> page = iUserBalanceService.findPage(userId, getPN());
		setAttr("page", page);
		setAttr("userId", userId);
	}

	public void edit() {
		int id = getParaToInt("id");
		UserBalance balance = iUserBalanceService.getById(id);
		setAttr("userBalance", balance);
		setAttr("userList", iUserService.findAll());
	}

	public void update() {
		UserBalance b = getModel(UserBalance.class, "userBalance");
		boolean ok = iUserBalanceService.updateBalance(b.getUserId(), b.getBalance().intValue(), b.getType(),
				PaySource.BOSS_INPUT, PayType.SYSTEM, b.getPrice(), b.getTotalPrice(), b.getRemark(), true);
		renderResultJson(ok);
	}
	
	/**
	 * 
	   * TODO 跳转至告警信息页面
	 */
	public void warning() {
		int id = getParaToInt("id");
		UserBalance balance = iUserBalanceService.getById(id);
		setAttr("userBalance", balance);
		setAttr("userModel", iUserService.getByUserId(balance.getUserId()));
	}
	
	public void warning_submit() {
		renderResultJson(iUserBalanceService.updateBalanceWarning(getModel(UserBalance.class, "userBalance")));
	}
	
	/**
	 * 
	   * TODO 修改状态
	 */
	public void update_status() {
		renderResultJson(iUserBalanceService.updateStatus(getParaToInt("id"), getParaToInt("status")));
	}

	/**
	 * 用户余额日志
	 */
	public void log() {
		String userId = getPara("userId");
		String platformType = getPara("platformType");
		BossPaginationVo<UserBalanceLog> page = iUserBalanceLogService.findPageBoss(getPN(), userId,platformType);
		setAttr("page", page);
		setAttr("userId", userId);
		setAttr("platformType", platformType);
	}

}
