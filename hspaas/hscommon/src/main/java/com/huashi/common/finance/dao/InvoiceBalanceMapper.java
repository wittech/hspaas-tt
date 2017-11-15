package com.huashi.common.finance.dao;

import com.huashi.common.finance.domain.InvoiceBalance;

public interface InvoiceBalanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InvoiceBalance record);

    int insertSelective(InvoiceBalance record);

    InvoiceBalance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InvoiceBalance record);

    int updateByPrimaryKey(InvoiceBalance record);
    
    /**
	 * 
	 * TODO 根据用户获取可开发票金额
	 * 
	 * @param userId
	 * @return
	 */
	InvoiceBalance getByUserId(int userId);
}