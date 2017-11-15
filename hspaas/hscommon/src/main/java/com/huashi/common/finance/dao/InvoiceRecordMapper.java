package com.huashi.common.finance.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.finance.domain.InvoiceRecord;

public interface InvoiceRecordMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(InvoiceRecord record);

	int insertSelective(InvoiceRecord record);

	InvoiceRecord selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(InvoiceRecord record);

	int updateByPrimaryKey(InvoiceRecord record);

	/**
	 * 
	 * TODO 总条数
	 * 
	 * @param params
	 * @return
	 */
	int getCountByUserId(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<InvoiceRecord> findPageListByUserId(Map<String, Object> params);

	List<InvoiceRecord> findList(Map<String, Object> paramMap);

	int findCount(Map<String, Object> paramMap);

	InvoiceRecord findById(Integer id);
}