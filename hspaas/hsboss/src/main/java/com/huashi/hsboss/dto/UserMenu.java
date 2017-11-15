package com.huashi.hsboss.dto;

import java.util.ArrayList;
import java.util.List;

public class UserMenu {

	
	private int id;
	
	private String menuName;
	
	private String menuCode;
	
	private String menuUrl;
	
	private int parentId;
	
	private List<UserMenu> childList = new ArrayList<UserMenu>();
	
	private List<UserOper> operList = new ArrayList<UserOper>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public List<UserMenu> getChildList() {
		return childList;
	}

	public void setChildList(List<UserMenu> childList) {
		this.childList = childList;
	}

	public List<UserOper> getOperList() {
		return operList;
	}

	public void setOperList(List<UserOper> operList) {
		this.operList = operList;
	}
	
	public boolean getHasChildMenu(){
		return !childList.isEmpty();
	}
	
	public boolean hasChild(){
		return !childList.isEmpty();
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
}
