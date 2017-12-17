package com.huashi.common.vo;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PaginationVo<T> implements Serializable {

	private static final long serialVersionUID = 2800200910531974922L;
	public static final int DEFAULT_START_PAGE_NO = 1; // 默认第一页
	public static final int DEFAULT_RECORD_PER_PAGE = 20;// 默认显示20条记录

	private List<T> list; // 数据集合
	private int currentPage = 1; // 当前页
	private int totalPage; // 总页数
	private int totalRecord; // 总记录数
	private int pageSize = DEFAULT_RECORD_PER_PAGE; // 一页显示多少条(暂时君采用默认值)

	public PaginationVo(List<T> list, int currentPage, int totalRecord) {
		super();
		this.list = list;
		this.currentPage = currentPage;
		this.totalRecord = totalRecord;
		if (totalRecord == 0) {
			setTotalPage(1);
		} else if (totalRecord % DEFAULT_RECORD_PER_PAGE == 0) {
			setTotalPage(totalRecord / DEFAULT_RECORD_PER_PAGE);
		} else {
			setTotalPage(totalRecord / DEFAULT_RECORD_PER_PAGE + 1);
		}
	}
	
	public PaginationVo(){
		super();
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public static int getStartPage(int currentPage) {
		if (currentPage == 0) {
            {
                currentPage = 1;
            }
        }
		return (currentPage - 1) * DEFAULT_RECORD_PER_PAGE;
	}

	/**
	 * 转换当前页
	 * 
	 * @param currentPage
	 * @return
	 */
	public static int parse(String currentPage) {
		try {
			return StringUtils.isEmpty(currentPage) ? PaginationVo.DEFAULT_START_PAGE_NO
					: Integer.parseInt(currentPage);
		} catch (Exception e) {
			return PaginationVo.DEFAULT_START_PAGE_NO;
		}
	}

}
