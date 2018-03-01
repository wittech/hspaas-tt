package com.huashi.common.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BossPaginationVo<T> implements Serializable {

	private static final long serialVersionUID = -7137047463344854550L;

	public static final int DEFAULT_PAGE_SIZE = 20;

	private int currentPage; // 当前页

	private int pageSize; // 每页容量

	private int totalCount; // 总数据

	private int totalPage; // 总页数

	private List<T> list = new ArrayList<T>(); // 数据集

	private String jumpPageFunction;

    private boolean showJumpButton = true;

    public boolean isShowJumpButton() {
        return showJumpButton;
    }

    public void setShowJumpButton(boolean showJumpButton) {
        this.showJumpButton = showJumpButton;
    }

    public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		if (pageSize <= 0) {
			return DEFAULT_PAGE_SIZE;
		}
		return pageSize;
	}

	public BossPaginationVo(){

	}

	public BossPaginationVo(int pageNum,int totalCount,List<T> dataList){
		this.currentPage = pageNum;
		this.totalCount = totalCount;
		this.list = dataList;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		int total = getTotalCount() / getPageSize();
		if (getTotalCount() % getPageSize() > 0) {
			total = total + 1;
		}
		totalPage = total;
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getShowPageHtml() {
		if (StringUtils.isBlank(jumpPageFunction)) {
			jumpPageFunction = "jumpPage";
		}
		String showPageHtml = "";
		if (getTotalPage() <= 1) {
			return "";
		}
		StringBuffer pathfind = new StringBuffer("");
		pathfind.append("<ul class='pagination' style='float:left'>");

		pathfind.append("<li class='disabled'><span style='color:#337ab7;'>共"
				+ getTotalPage() + "页"+getTotalCount()+"条</span></li>");

		if (getTotalPage() == 1 || getCurrentPage() == 1) {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>首页</a></li>");
		} else {
			pathfind.append("<li><a href='javascript:" + jumpPageFunction + "("
					+ 1 + ")'>首页</a></li>");
		}
		if (isHasPre()) {
			pathfind.append("<li><a href='javascript:" + jumpPageFunction + "("
					+ this.getPrePage() + ")'>上一页</a><li>");
		} else {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>上一页</a><li>");
		}

		int liststep = 9;

		if (getCurrentPage() < liststep) {
			for (int i = 1; i <= (getTotalPage() > liststep ? liststep
					: getTotalPage()); i++) {

				// 如果i不等于当前页
				if (i != getCurrentPage()) {
					pathfind.append("<li><a href='javascript:"
							+ jumpPageFunction + "(" + i + ");'>" + i
							+ "</a></li>");
				} else {
					pathfind.append("<li class='active'><a href='javascript:void(0);'>"
							+ i + "</a></li>");
				}
			}
		} else {
			if (getCurrentPage()
					- (liststep % 2 == 0 ? liststep / 2 - 1 : liststep / 2) > 1
					&& getCurrentPage() + liststep / 2 <= getTotalPage()) {
				for (int i = getCurrentPage()
						- (liststep % 2 == 0 ? liststep / 2 - 1 : liststep / 2); i <= getCurrentPage()
						+ liststep / 2; i++) {
					if (i != getCurrentPage()) {// 如果i不等于当前页
						pathfind.append("<li><a href='javascript:"
								+ jumpPageFunction + "(" + i + ");'>" + i
								+ "</a></li>");
					} else {
						pathfind.append("<li class='active'><a href='javascript:void(0);'>"
								+ i + "</a></li>");
					}
				}
			} else if (getCurrentPage() + liststep / 2 > getTotalPage()) {
				for (int i = (int) getTotalPage() - liststep + 1; i <= getTotalPage(); i++) {
					if (i != getCurrentPage()) {// 如果i不等于当前页
						pathfind.append("<li><a href='javascript:"
								+ jumpPageFunction + "(" + i + ");'>" + i
								+ "</a></li>");
					} else {
						pathfind.append("<li class='active'><a href='javascript:void(0);'>"
								+ i + "</a></li>");
					}
				}
			}
		}

		if (isHasNext()) {
			pathfind.append("<li><a href='javascript:" + jumpPageFunction + "("
					+ this.getNextPage() + ")' >下一页</a></li>");
		} else {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>下一页</a></li>");
		}
		if (getTotalPage() <= 1 || getTotalPage() == getCurrentPage()) {
			pathfind.append("<li class='disabled'><a href='javascript:void(0);'>尾页</a></li>");
		} else {
			pathfind.append("<li><a href='javascript:" + jumpPageFunction + "("
					+ this.getTotalPage() + ")'>尾页</a></li>");
		}

		pathfind.append("</ul>");

        if(isShowJumpButton()){
            pathfind.append(getInputJumpPageJsMethodStr());
            pathfind.append("<div class='input-group'>");
            pathfind.append("<input type='text' class='form-control' onkeydown='inputJumpPageKeydown();' style='width:40px;float:left;margin-left:10px;height:31px;margin-top:5px;' id='inputPageNumber'/>");
            pathfind.append("<span class='input-group-btn' style='width:40px;float:left;height:31px;margin-top:5px;'>" +
                                "<button class='btn btn-info' style='height:31px;' type='button' onclick='inputJumpPage();'>跳页</button>" +
                            "</span>");
            pathfind.append("</div>");
        }

		showPageHtml = pathfind.toString();
		return showPageHtml;
	}


	private String getInputJumpPageJsMethodStr(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("<script type='text/javascript'>");
        buffer.append("function inputJumpPage(){");
        buffer.append("var pn = $('#inputPageNumber').val();");
        buffer.append("var regex = /^[0-9]*[1-9][0-9]*$/;");
        buffer.append("if(!regex.test(pn)){");
        buffer.append("Boss.alert('请输入正确的页码!');");
        buffer.append("return;");
        buffer.append("}");
        buffer.append("if(pn > "+getTotalPage()+") {");
        buffer.append("pn = "+getTotalPage()+";");
        buffer.append("}");
        buffer.append("if(pn < 1) {");
        buffer.append("pn = 1;");
        buffer.append("}");
        buffer.append(jumpPageFunction+"(pn);");
        buffer.append("}");

        buffer.append("function inputJumpPageKeydown(){");
        buffer.append("if(event.keyCode==13){");
        buffer.append("inputJumpPage()");
        buffer.append("}");
        buffer.append("}");
        buffer.append("</script>");
        return buffer.toString();
    }

	public boolean isHasPre() {
		return getCurrentPage() > 1;
	}

	public boolean isHasNext() {
		return getCurrentPage() < getTotalPage();
	}

	public int getPrePage() {
		if (isHasPre()) {
			return getCurrentPage() - 1;
		} else {
			return getCurrentPage();
		}
	}

	public int getNextPage() {
		if (isHasNext()) {
			return getCurrentPage() + 1;
		} else {
			return getCurrentPage();
		}
	}

	public int getStartPosition() {
		if (currentPage == 0) {
            {
                currentPage = 1;
            }
        }

		if (pageSize <= 0) {
            {
                pageSize = DEFAULT_PAGE_SIZE;
            }
        }

		return (currentPage - 1) * pageSize;
	}

	public String getJumpPageFunction() {
		return jumpPageFunction;
	}

	public void setJumpPageFunction(String jumpPageFunction) {
		this.jumpPageFunction = jumpPageFunction;
	}

}
