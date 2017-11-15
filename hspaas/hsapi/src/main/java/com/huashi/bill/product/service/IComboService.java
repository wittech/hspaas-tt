package com.huashi.bill.product.service;

import java.util.List;

import com.huashi.bill.product.domain.Combo;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

public interface IComboService {

	boolean create(Combo combo, String productIds);

	boolean update(Combo combo, String productIds);

	boolean delete(int id);

	BossPaginationVo<Combo> findPage(int pageNum, String name, String start, String end);

	Combo findById(int id);

	boolean updateStatus(int id, int status);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param type
	 * @param currentPage
	 * @return
	 */
	PaginationVo<Combo> findPageList(String currentPage);
	
	
	/**
	 * 查询最新套餐
	 * @param article 条
	 * @return
	 */
	List<Combo> findLatestCombo(int article);
}
