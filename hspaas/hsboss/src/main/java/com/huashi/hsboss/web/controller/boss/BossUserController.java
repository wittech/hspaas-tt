package com.huashi.hsboss.web.controller.boss;

import java.util.List;
import java.util.Map;

import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.model.boss.BossRole;
import com.huashi.hsboss.model.boss.BossUser;
import com.huashi.hsboss.service.boss.BossRoleService;
import com.huashi.hsboss.service.boss.BossUserService;
import com.huashi.hsboss.service.common.PageExt;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 用户管理
 * @author ym
 * @created_at 2016年6月28日下午5:32:58
 */
@ViewMenu(code=MenuCode.MENU_CODE_6001)
@ControllerBind(controllerKey="/boss/user")
public class BossUserController extends BaseController {

	
	@BY_NAME
	private BossUserService bossUserService;
	@BY_NAME
	private BossRoleService bossRoleService;

	@AuthCode(code= {OperCode.OPER_CODE_6001001,OperCode.OPER_CODE_6001002,OperCode.OPER_CODE_6001003})
	@ActionMode
	public void index(){
		String keyword = getPara("keyword");
		PageExt<BossUser> page = bossUserService.findPage(getPN(), keyword);
		setAttr("page", page);
		setAttr("keyword", keyword);
	}

	@AuthCode(code= OperCode.OPER_CODE_6001001)
	@ActionMode
	public void add(){
		List<BossRole> roleList = bossRoleService.findAll();
		setAttr("roleList", roleList);
	}

	@AuthCode(code= OperCode.OPER_CODE_6001002)
	@ActionMode
	public void edit(){
		List<BossRole> roleList = bossRoleService.findAll();
		List<BossRole> userRoleList = bossRoleService.getUserRoleList(getParaToInt("id"));
		BossUser bossUser = BossUser.DAO.findById(getParaToInt("id"));
		setAttr("bossUser", bossUser);
		setAttr("roleList", roleList);
		setAttr("userRoleList", userRoleList);
	}

	@AuthCode(code= OperCode.OPER_CODE_6001001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create(){
		BossUser bossUser = getModel(BossUser.class);
		String roleIds = getPara("roleIds");
		Map<String, Object> map = bossUserService.create(bossUser, getLoginName(), roleIds);
		renderJson(map);
	}

	@AuthCode(code= OperCode.OPER_CODE_6001002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		BossUser bossUser = getModel(BossUser.class);
		String roleIds = getPara("roleIds");
		Map<String, Object> map = bossUserService.update(bossUser,roleIds);
		renderJson(map);
	}

	@AuthCode(code= OperCode.OPER_CODE_6001003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete(){
		Map<String, Object> map = bossUserService.delete(getParaToInt("id"));
		renderJson(map);
	}

	@AuthCode(code= OperCode.OPER_CODE_6001002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void disabled(){
		Map<String, Object> map = bossUserService.disabled(getParaToInt("id"),getParaToInt("flag"));
		renderJson(map);
	}
	
	/**
	 * 修改密码页面
	 */
	@AuthCode(code= OperCode.OPER_CODE_COMMON)
	@ActionMode
	public void password(){
		
	}
	
	/**
	 * 修改密码 方法
	 */
	@AuthCode(code= OperCode.OPER_CODE_COMMON)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void updatePassword(){
		renderJson(bossUserService.updateNewPassword(getUserId(),getPara("originalPassword"),getPara("newPassword")));
	}
	
	
}
