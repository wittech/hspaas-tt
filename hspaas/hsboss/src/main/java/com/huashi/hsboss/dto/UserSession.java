package com.huashi.hsboss.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 存储用户登录信息
 * @author ym
 * @created_at 2016年6月22日下午2:10:17
 */
public class UserSession {

	
	private int userId;
	
	private String loginName;
	
	private String realName;
	
	private Date loginTime;
	
	private String loginIp;
	
	private Date lastLoginTime;
	
	private String lastLoginIp;
	
	private int viewTopMenuId;
	
	private List<UserMenu> menuList = new ArrayList<UserMenu>();

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public List<UserMenu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<UserMenu> menuList) {
		this.menuList = menuList;
	}

	public UserMenu getLeftMenu(){
		if(menuList.isEmpty()){
			return null;
		}
		if(viewTopMenuId <= 0){
			return menuList.get(0);
		}
		UserMenu currentMenu = null;
		for(UserMenu menu : menuList){
			if(menu.getId() == viewTopMenuId){
				currentMenu = menu;
				break;
			}
		}
		if(currentMenu == null){
			currentMenu = menuList.get(0);
		}
		return currentMenu;
	}

	public int getViewTopMenuId() {
		return viewTopMenuId;
	}

	public void setViewTopMenuId(int viewTopMenuId) {
		this.viewTopMenuId = viewTopMenuId;
	}
	
	
}
