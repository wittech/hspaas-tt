package com.huashi.hsboss.web.controller.boss;

import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 日志管理
 * @author ym
 * @created_at 2016年6月28日下午5:32:32
 */
@ViewMenu(code=MenuCode.MENU_CODE_6003)
@ControllerBind(controllerKey="/boss/log")
public class BossLogController extends BaseController{

	
	public void index(){
		
	}
}
