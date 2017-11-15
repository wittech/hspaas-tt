package com.huashi.hsboss.service.boss;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.huashi.hsboss.dto.UserMenu;
import com.huashi.hsboss.model.boss.BossMenu;
import com.huashi.hsboss.service.common.BaseService;

@Service
public class BossMenuService extends BaseService{

	
	public List<BossMenu> findTop(){
		return BossMenu.DAO.find("select * from hsboss_menu h where h.parent_id = ?",-1);
	}
	
	public List<BossMenu> findChild(int parentId){
		return BossMenu.DAO.find("select * from hsboss_menu h where h.parent_id = ? order by h.menu_position asc",parentId);
	}
	
	public List<UserMenu> findAllMenu(){
		List<UserMenu> menuList = new ArrayList<UserMenu>();
		List<BossMenu> topList = BossMenu.DAO.find("select * from hsboss_menu h where h.parent_id = ?",-1);
		for(BossMenu top : topList){
			UserMenu firstMenu = null;
			firstMenu = new UserMenu();
			firstMenu.setId(top.getInt("id"));
			firstMenu.setMenuName(top.getStr("menu_name"));
			firstMenu.setMenuCode(top.getStr("menu_code"));
			firstMenu.setMenuUrl(top.getStr("menu_url"));
			menuList.add(firstMenu);
		}
		
		List<UserMenu> menuChildList = new ArrayList<UserMenu>();
		List<BossMenu> childList = BossMenu.DAO.find("select * from hsboss_menu h where h.parent_id > ?",-1);
		out:for(BossMenu child : childList){
			UserMenu childMenu = new UserMenu();
			childMenu.setId(child.getInt("id"));
			childMenu.setMenuName(child.getStr("menu_name"));
			childMenu.setMenuCode(child.getStr("menu_code"));
			childMenu.setMenuUrl(child.getStr("menu_url"));
			childMenu.setParentId(child.getInt("parent_id"));
			for(UserMenu thirdMenu : menuChildList){
				if(thirdMenu.getId() == child.getInt("parent_id")){
					thirdMenu.getChildList().add(childMenu);
					continue out;
				}
			}
			menuChildList.add(childMenu);
		}
		
		for(UserMenu userMenu : menuList){
			for(UserMenu childMenu : menuChildList){
				if(userMenu.getId() == childMenu.getParentId()){
					userMenu.getChildList().add(childMenu);
				}
			}
		}
		return menuList;
	}
	
	public List<UserMenu> getAllMenu(){
		List<UserMenu> menuList = new ArrayList<UserMenu>();
		List<BossMenu> topList = BossMenu.DAO.find("select * from hsboss_menu h where h.parent_id = ?",-1);
		for(BossMenu menu : topList){
			UserMenu userMenu = new UserMenu();
			userMenu.setId(menu.getInt("id"));
			userMenu.setMenuName(menu.getStr("menu_name"));
			userMenu.setMenuUrl(menu.getStr("menu_url"));
			userMenu.setMenuCode(menu.getStr("menu_code"));
			
			List<BossMenu> childList = findChild(menu.getInt("id"));
			List<UserMenu> childMenuList = new ArrayList<UserMenu>();
			for(BossMenu childMenu : childList){
				UserMenu userChildMenu = new UserMenu();
				userChildMenu.setId(childMenu.getInt("id"));
				userChildMenu.setMenuName(childMenu.getStr("menu_name"));
				userChildMenu.setMenuUrl(childMenu.getStr("menu_url"));
				userChildMenu.setMenuCode(childMenu.getStr("menu_code"));
				childMenuList.add(userChildMenu);
				
				List<UserMenu> thirdMenuList = new ArrayList<UserMenu>();
				List<BossMenu> thirdList = findChild(childMenu.getInt("id"));
				for(BossMenu thirdMenu : thirdList){
					UserMenu thirdChildMenu = new UserMenu();
					thirdChildMenu.setId(thirdMenu.getInt("id"));
					thirdChildMenu.setMenuName(thirdMenu.getStr("menu_name"));
					thirdChildMenu.setMenuUrl(thirdMenu.getStr("menu_url"));
					thirdChildMenu.setMenuCode(thirdMenu.getStr("menu_code"));
					thirdMenuList.add(thirdChildMenu);
				}
				userChildMenu.getChildList().addAll(thirdMenuList);
			}
			userMenu.getChildList().addAll(childMenuList);
			menuList.add(userMenu);
		}
		return menuList;
	}
}
