package com.huashi.hsboss.web.controller.boss;

import java.util.Map;

import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.model.boss.BossRole;
import com.huashi.hsboss.service.boss.BossMenuService;
import com.huashi.hsboss.service.boss.BossRoleService;
import com.huashi.hsboss.service.common.PageExt;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 角色管理
 * @author ym
 * @created_at 2016年6月28日下午5:32:38
 */
@ViewMenu(code=MenuCode.MENU_CODE_6002)
@ControllerBind(controllerKey="/boss/role")
public class BossRoleController extends BaseController {

	
	@BY_NAME
	private BossRoleService bossRoleService;

	@BY_NAME
	private BossMenuService bossMenuService;

	@AuthCode(code= {OperCode.OPER_CODE_6002001, OperCode.OPER_CODE_6002002,OperCode.OPER_CODE_6002003})
	@ActionMode
	public void index(){
		PageExt<BossRole> page = bossRoleService.findPage(getPN());
		setAttr("page", page);
	}

	@AuthCode(code= {OperCode.OPER_CODE_6002001})
	@ActionMode
	public void add(){
		
	}

	@AuthCode(code= OperCode.OPER_CODE_6002001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create(){
		BossRole role = getModel(BossRole.class);
		Map<String, Object> map = bossRoleService.create(role, getLoginName());
		renderJson(map);
	}

	@AuthCode(code= OperCode.OPER_CODE_6002002)
	@ActionMode
	public void edit(){
		BossRole role = BossRole.DAO.findById(getPara("id"));
		setAttr("role", role);
	}

	@AuthCode(code= OperCode.OPER_CODE_6002002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		BossRole role = getModel(BossRole.class);
		Map<String, Object> map = bossRoleService.update(role);
		renderJson(map);
	}

	@AuthCode(code= OperCode.OPER_CODE_6002003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete(){
		Map<String, Object> map = bossRoleService.delete(getParaToInt("id"));
		renderJson(map);
	}

	@AuthCode(code= OperCode.OPER_CODE_6002004)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void authTree(){
		int roleId = getParaToInt("id");
		renderJson(bossRoleService.getOperByRoleId(roleId));
	}

	@AuthCode(code= OperCode.OPER_CODE_6002004)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void saveAuth() {
		int roleId = getParaToInt("id");
		String operIds = getPara("operIds");
		renderJson(bossRoleService.saveAuth(roleId,operIds));
	}



}
