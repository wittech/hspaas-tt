package com.huashi.bill.product.service;

import java.util.List;
import java.util.Map;

import com.huashi.bill.product.domain.Product;
import com.huashi.common.vo.BossPaginationVo;

public interface IProductService {

	boolean create(Product product);

	boolean update(Product product);

	Map<String, Object> delete(int id);

	BossPaginationVo<Product> findPage(int pn, String name);

	Product findById(int id);

	boolean disabled(int id, int flag);

	List<Product> getProductListByComboId(int comboId);

	List<Product> getSelectProductList(String name);
}
