package com.huashi.bill.order.dao;

import com.huashi.bill.order.domain.ExchangeOrder;

public interface ExchangeOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExchangeOrder record);

    int insertSelective(ExchangeOrder record);

    ExchangeOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExchangeOrder record);

    int updateByPrimaryKey(ExchangeOrder record);
}