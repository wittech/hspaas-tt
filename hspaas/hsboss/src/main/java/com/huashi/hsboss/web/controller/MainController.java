package com.huashi.hsboss.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.huashi.hsboss.dto.UserMenu;
import com.huashi.hsboss.dto.UserSession;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 主界面控制
 * @author ym
 * @created_at 2016年6月22日下午2:11:52
 */
@ControllerBind(controllerKey="/main")
public class MainController extends BaseController{
	
	public void index(){
	}
	
	public void top(){
		int topMenuId = getParaToInt(0,0);
		getUserSession().setViewTopMenuId(topMenuId);
        UserSession userSession = getUserSession();
        String jumpUrl = "/main";
        if(topMenuId > 0){
            List<UserMenu> menuList = userSession.getMenuList();
            for(UserMenu menu : menuList) {
                if(menu.getId() == topMenuId){
                    UserMenu childMenu = menu.getChildList().get(0);
                    if(!childMenu.getChildList().isEmpty()){
                        childMenu = childMenu.getChildList().get(0);
                    }
                    jumpUrl = childMenu.getMenuUrl();
                    break;
                }
            }
        }
        if(StringUtils.isBlank(jumpUrl)){
            jumpUrl = "/main";
        }
		redirect(jumpUrl);
	}
}
 