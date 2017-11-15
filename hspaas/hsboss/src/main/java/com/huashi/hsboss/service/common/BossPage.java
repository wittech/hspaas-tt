package com.huashi.hsboss.service.common;

import com.huashi.common.vo.PaginationVo;

public class BossPage<T> extends PaginationVo<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public String getShowPageHtml() {
		String showPageHtml = "";
		if(getTotalPage() <= 1){
			return "";
		}
		StringBuffer pathfind = new StringBuffer("");
		pathfind.append("<ul class='pagination'>");
		
		pathfind.append("<li class='disabled'><span style='color:#337ab7;'>共"+getTotalPage()+"页</span></li>");
		
		if (getTotalPage() == 1 || getCurrentPage() == 1) {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>首页</a></li>");
		} else {
			pathfind.append("<li><a href='javascript:jumpPage(" + 1 + ")'>首页</a></li>");
		}
		if (isHasPre()) {
			pathfind.append("<li><a href='javascript:jumpPage("+ this.getPrePage() + ")'>上一页</a><li>");
		} else {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>上一页</a><li>");
		}

		int liststep = 9;

		if (getCurrentPage() < liststep) {
			for (int i = 1; i <= (getTotalPage() > liststep ? liststep: getTotalPage()); i++) {
				if (i != getCurrentPage()) {// 如果i不等于当前页
					pathfind.append("<li><a href='javascript:jumpPage("+ i + ");'>" + i + "</a></li>");
				} else {
					pathfind.append("<li class='active'><a href='javascript:void(0);'>"+ i + "</a></li>");
				}
			}
		} else {
			if (getCurrentPage() - (liststep % 2 == 0 ? liststep / 2 - 1 : liststep / 2) > 1 && getCurrentPage() + liststep / 2 <= getTotalPage()) {
				for (int i = getCurrentPage()- (liststep % 2 == 0 ? liststep / 2 - 1 : liststep / 2); i <= getCurrentPage() + liststep / 2; i++) {
					if (i != getCurrentPage()) {// 如果i不等于当前页
						pathfind.append("<li><a href='javascript:jumpPage("+ i+ ");'>"+ i+ "</a></li>");
					} else {
						pathfind.append("<li class='active'><a href='javascript:void(0);'>"+ i + "</a></li>");
					}
				}
			} else if (getCurrentPage() + liststep / 2 > getTotalPage()) {
				for (int i = (int) getTotalPage() - liststep + 1; i <= getTotalPage(); i++) {
					if (i != getCurrentPage()) {// 如果i不等于当前页
						pathfind.append("<li><a href='javascript:jumpPage("+ i+ ");'>"+ i+ "</a></li>");
					} else {
						pathfind.append("<li class='active'><a href='javascript:void(0);'>"+ i + "</a></li>");
					}
				}
			}
		}

		if (isHasNext()) {
			pathfind.append("<li><a href='javascript:jumpPage("+ this.getNextPage() + ")' >下一页</a></li>");
		} else {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>下一页</a></li>");
		}
		if (getTotalPage() <= 1 || getTotalPage() == getCurrentPage()) {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>尾页</a></li>");
		} else {
			pathfind.append("<li><a href='javascript:jumpPage("+ this.getTotalPage() + ")'>尾页</a></li>");
		}
		pathfind.append("</ul>");
		showPageHtml = pathfind.toString();
		return showPageHtml;
	}
	
	public boolean isHasPre(){
		return getCurrentPage() > 1;
	}
	
	public boolean isHasNext(){
		return getCurrentPage() < getTotalPage();
	}
	
	public int getPrePage(){
		if(isHasPre()){
			return getCurrentPage() - 1;
		}else{
			return getCurrentPage();
		}
	}
	
	public int getNextPage(){
		if(isHasNext()){
			return getCurrentPage() + 1;
		}else{
			return getCurrentPage();
		}
	}

}
