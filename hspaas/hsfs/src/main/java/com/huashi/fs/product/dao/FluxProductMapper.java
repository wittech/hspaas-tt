package com.huashi.fs.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.fs.product.domain.FluxProduct;

public interface FluxProductMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(FluxProduct record);

	int insertSelective(FluxProduct record);

	FluxProduct selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(FluxProduct record);

	int updateByPrimaryKey(FluxProduct record);

	/**
	 * 根据运营商id获取套餐列表 参数为空则查询全部套餐列表信息
	 * 
	 * @param operatorId
	 * @return
	 */
	List<String> selectAllist(int operatorId);

	/**
	 * 
	 * TODO 根据面值和运营商查询流量产品信息
	 * 
	 * @param parValue
	 * @param operator
	 * @return
	 */
	List<FluxProduct> selectByParValue(@Param("parValue") String parValue, @Param("operator") int operator);
}