package com.huashi.hsboss.web.controller.boss;

import java.util.List;
import java.util.Map;

import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
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
	
	public void index(){
		String keyword = getPara("keyword");
		PageExt<BossUser> page = bossUserService.findPage(getPN(), keyword);
		setAttr("page", page);
		setAttr("keyword", keyword);
	}
	
	public void add(){
		List<BossRole> roleList = bossRoleService.findAll();
		setAttr("roleList", roleList);
	}
	
	public void edit(){
		List<BossRole> roleList = bossRoleService.findAll();
		List<BossRole> userRoleList = bossRoleService.getUserRoleList(getParaToInt("id"));
		BossUser bossUser = BossUser.DAO.findById(getParaToInt("id"));
		setAttr("bossUser", bossUser);
		setAttr("roleList", roleList);
		setAttr("userRoleList", userRoleList);
	}
	
	public void create(){
		BossUser bossUser = getModel(BossUser.class);
		String roleIds = getPara("roleIds");
		Map<String, Object> map = bossUserService.create(bossUser, getLoginName(), roleIds);
		renderJson(map);
	}
	
	public void update(){
		BossUser bossUser = getModel(BossUser.class);
		String roleIds = getPara("roleIds");
		Map<String, Object> map = bossUserService.update(bossUser,roleIds);
		renderJson(map);
	}
	
	public void delete(){
		Map<String, Object> map = bossUserService.delete(getParaToInt("id"));
		renderJson(map);
	}
	
	public void disabled(){
		Map<String, Object> map = bossUserService.disabled(getParaToInt("id"),getParaToInt("flag"));
		renderJson(map);
	}
	
	/**
	 * 修改密码页面
	 */
	public void password(){
		
	}
	
	/**
	 * 修改密码 方法
	 */
	public void updatePassword(){
		renderJson(bossUserService.updateNewPassword(getUserId(),getPara("originalPassword"),getPara("newPassword")));
	}
	
	
}
