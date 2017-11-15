package com.huashi.bill.product.dao;

import java.util.List;
import java.util.Map;

import com.huashi.bill.product.domain.Combo;
import org.apache.ibatis.annotations.Param;

public interface ComboMapper {

	/**
	 * 
	 * @param id
	 *            产品ID
	 * @return
	 */
	List<Combo> getComboByProdctId(int id);

	List<Combo> findList(Map<String, Object> paramMap);

	int getCount(Map<String, Object> paramMap);

	Combo findById(int id);

	int updateStatus(@Param("id") int id, @Param("status") int status);

	int insert(Combo combo);

	int update(Combo combo);

	int delete(int id);

	/**
	 * 
	 * TODO 总条数
	 * 
	 * @param params
	 * @return
	 */
	int getCountNum(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<Combo> findPageList(Map<String, Object> params);

}
