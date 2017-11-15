package com.huashi.bill.product.dao;

import java.util.List;
import java.util.Map;

import com.huashi.bill.product.domain.Product;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {

	Product findById(int id);

	int getCount(Map<String, Object> paramMap);

	List<Product> findList(Map<String, Object> paramMap);

	int updateStatus(@Param("id") int id,@Param("status") int status);

	int delete(int id);

	int insert(Product product);

	int update(Product product);

	List<Product> getProductListByComboId(int comboId);

	List<Product> getSelectProductList(String name);
}
