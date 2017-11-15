package com.huashi.bill.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.bill.order.domain.TradeOrder;

public interface TradeOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TradeOrder record);

    int insertSelective(TradeOrder record);

    TradeOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TradeOrder record);

    int updateByPrimaryKey(TradeOrder record);
    
    /**
	 * 
	 * TODO 根据交易订单号查询订单
	 * 
	 * @param tradeNo
	 * @return
	 */
	TradeOrder selectByTradeNo(String tradeNo);

	/**
	 * 
	 * TODO 更新订单至完成
	 * 
	 * @param id
	 * @return
	 */
	int updateOrder2Comoplte(@Param("id") Long id);
	
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
	List<TradeOrder> findPageListByUserId(Map<String, Object> params);

	List<TradeOrder> findList(Map<String,Object> params);

	int findCount(Map<String,Object> params);
}
