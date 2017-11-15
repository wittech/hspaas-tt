package com.huashi.hsboss.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.constant.SystemConstant;
import com.huashi.hsboss.dto.UserMenu;
import com.huashi.hsboss.dto.UserSession;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class ViewContextInterceptor implements Interceptor {

	
	private static final Logger LOG = LoggerFactory.getLogger(ViewContextInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		HttpServletRequest request = inv.getController().getRequest();
		UserSession session = inv.getController().getSessionAttr(SystemConstant.USER_SESSION);
		if(session != null){
			UserMenu leftMenu = session.getLeftMenu();
			request.setAttribute("session", session);
			request.setAttribute("leftMenu", leftMenu);
			long startTime = System.currentTimeMillis();
			BaseController controller = (BaseController)inv.getController();
			String actionKey = inv.getActionKey();
			ViewMenu viewMenu = inv.getMethod().getAnnotation(ViewMenu.class);
			if(viewMenu == null){
				viewMenu = controller.getClass().getAnnotation(ViewMenu.class);
			}
			int secondMenuId = 0;
			int thirdMenuId = 0;
			if(viewMenu != null && viewMenu.code().length > 0){
				String[] menuCodes = viewMenu.code();
				out:for(String menuCode : menuCodes){
					List<UserMenu> secondList = leftMenu.getChildList();
					for(UserMenu secondMenu : secondList){
						boolean has = false;
						if(secondMenu.getMenuCode().equals(menuCode)){
							secondMenuId = secondMenu.getId();
							has = true;
						}
						if(!has && secondMenu.hasChild()){
							List<UserMenu> thirdList = secondMenu.getChildList();
							for(UserMenu thirdMenu : thirdList){
								if(thirdMenu.getMenuCode().equals(menuCode)){
									if(menuCodes.length > 1 && !thirdMenu.getMenuUrl().equals(actionKey)){
										continue;
									}
									secondMenuId = secondMenu.getId();
									thirdMenuId = thirdMenu.getId();
									has = true;
									break out;
								}
							}
						}
						if(has){
							break out;
						}
					}
				}
			}
			request.setAttribute("secondMenuId", secondMenuId);
			request.setAttribute("thirdMenuId", thirdMenuId);
			LOG.info("menu dispose use time:"+(System.currentTimeMillis() - startTime)+"ms");
		}
		inv.invoke();
		
	}
	
	

}
