package com.huashi.common.vo;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 
  * TODO 分页基础类
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2018年1月31日 上午10:46:45
 */
public class PaginationVo<T> implements Serializable {

	private static final long serialVersionUID = 2800200910531974922L;

	/**
	 * 默认第一页
	 */
	private static final int DEFAULT_START_PAGE_NO = 1;

	/**
	 * 默认显示20条记录
	 */
	public static final int DEFAULT_RECORD_PER_PAGE = 20;

	/**
	 * 数据集合
	 */
	private List<T> list;

	/**当前页码
	 *
	 */
	private int currentPage = 1;

	/**
	 * 总页数
	 */
	private int totalPage;

	/**
	 * 总记录数
	 */
	private int totalRecord;

	/**
	 * 一页显示多少条
	 */
	private int pageSize = DEFAULT_RECORD_PER_PAGE;

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

	protected int getTotalPage() {
		return totalPage;
	}

	private void setTotalPage(int totalPage) {
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
