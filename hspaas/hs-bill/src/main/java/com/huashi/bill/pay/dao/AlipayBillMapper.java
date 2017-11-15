package com.huashi.bill.pay.dao;

import com.huashi.bill.pay.domain.AlipayBill;

public interface AlipayBillMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AlipayBill record);

    int insertSelective(AlipayBill record);

    AlipayBill selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AlipayBill record);

    int updateByPrimaryKey(AlipayBill record);
}