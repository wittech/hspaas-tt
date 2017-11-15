package com.huashi.hsboss.web.controller.boss;

import java.util.Map;

import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.model.boss.BossRole;
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
	
	public void index(){
		PageExt<BossRole> page = bossRoleService.findPage(getPN());
		setAttr("page", page);
	}
	
	public void add(){
		
	}
	
	public void create(){
		BossRole role = getModel(BossRole.class);
		Map<String, Object> map = bossRoleService.create(role, getLoginName());
		renderJson(map);
	}
	
	public void edit(){
		BossRole role = BossRole.DAO.findById(getPara("id"));
		setAttr("role", role);
	}
	
	public void update(){
		BossRole role = getModel(BossRole.class);
		Map<String, Object> map = bossRoleService.update(role);
		renderJson(map);
	}
	
	public void delete(){
		Map<String, Object> map = bossRoleService.delete(getParaToInt("id"));
		renderJson(map);
	}
}
