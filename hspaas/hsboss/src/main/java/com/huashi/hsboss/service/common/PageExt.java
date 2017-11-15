package com.huashi.hsboss.service.common;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;

/**
 * 重写Jfinal的分页
 * @author yangm
 * @create_at 2015年4月20日 下午2:48:31
 */
public class PageExt<T> extends Page<T> {

	private static final long serialVersionUID = 1L;
	
	
	private String showPageHtml;
	
	public PageExt(List<T> list, int pageNumber, int pageSize, int totalPage,int totalRow) {
		super(list, pageNumber, pageSize, totalPage, totalRow);
	}
	
	public boolean isHasPre(){
		return getPageNumber() > 1;
	}
	
	public boolean isHasNext(){
		return getPageNumber() < getTotalPage();
	}
	
	public int getPrePage(){
		if(isHasPre()){
			return getPageNumber() - 1;
		}else{
			return getPageNumber();
		}
	}
	
	public int getNextPage(){
		if(isHasNext()){
			return getPageNumber() + 1;
		}else{
			return getPageNumber();
		}
	}

	public String getShowPageHtml() {
		if(getTotalPage() <= 1){
			return "";
		}
		StringBuffer pathfind = new StringBuffer("");
		pathfind.append("<ul class='pagination'>");
		
		pathfind.append("<li class='disabled'><span style='color:#337ab7;'>共"+getTotalPage()+"页</span></li>");
		
		if (getTotalPage() == 1 || getPageNumber() == 1) {
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

		if (getPageNumber() < liststep) {
			for (int i = 1; i <= (getTotalPage() > liststep ? liststep: getTotalPage()); i++) {
				if (i != getPageNumber()) {// 如果i不等于当前页
					pathfind.append("<li><a href='javascript:jumpPage("+ i + ");'>" + i + "</a></li>");
				} else {
					pathfind.append("<li class='active'><a href='javascript:void(0);'>"+ i + "</a></li>");
				}
			}
		} else {
			if (getPageNumber() - (liststep % 2 == 0 ? liststep / 2 - 1 : liststep / 2) > 1 && getPageNumber() + liststep / 2 <= getTotalPage()) {
				for (int i = getPageNumber()- (liststep % 2 == 0 ? liststep / 2 - 1 : liststep / 2); i <= getPageNumber() + liststep / 2; i++) {
					if (i != getPageNumber()) {// 如果i不等于当前页
						pathfind.append("<li><a href='javascript:jumpPage("+ i+ ");'>"+ i+ "</a></li>");
					} else {
						pathfind.append("<li class='active'><a href='javascript:void(0);'>"+ i + "</a></li>");
					}
				}
			} else if (getPageNumber() + liststep / 2 > getTotalPage()) {
				for (int i = (int) getTotalPage() - liststep + 1; i <= getTotalPage(); i++) {
					if (i != getPageNumber()) {// 如果i不等于当前页
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
		if (getTotalPage() <= 1 || getTotalPage() == getPageNumber()) {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>尾页</a></li>");
		} else {
			pathfind.append("<li><a href='javascript:jumpPage("+ this.getTotalPage() + ")'>尾页</a></li>");
		}
		pathfind.append("</ul>");
		showPageHtml = pathfind.toString();
		return showPageHtml;
	}

	public void setShowPageHtml(String showPageHtml) {
		this.showPageHtml = showPageHtml;
	}

	
	
}
