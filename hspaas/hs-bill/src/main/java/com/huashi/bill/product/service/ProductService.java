package com.huashi.bill.product.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.bill.product.dao.ComboMapper;
import com.huashi.bill.product.dao.ProductMapper;
import com.huashi.bill.product.domain.Combo;
import com.huashi.bill.product.domain.Product;
import com.huashi.common.vo.BossPaginationVo;

/**
 * 套餐产品
 * @author ym
 * @created_at 2016年7月12日下午5:19:01
 */
@Service
public class ProductService implements IProductService {

	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ComboMapper comboMapper;
	
	@Override
	@Transactional(readOnly=false)
	public boolean create(Product product) {
		try {
			productMapper.insert(product);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Transactional(readOnly=false)
	public boolean update(Product product) {
		try {
			productMapper.update(product);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	@Transactional(readOnly=false)
	public Map<String, Object> delete(int id) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("result", true);
		map.put("message", "删除成功！");
		try {
			List<Combo> comboList = comboMapper.getComboByProdctId(id);
			if(comboList != null && !comboList.isEmpty()){
				map.put("result", false);
				map.put("message", "该产品正在套餐中使用，无法删除！");
			}else{
				productMapper.delete(id);
			}
		} catch (Exception e) {
			map.put("result", false);
			map.put("message", "删除失败！");
		}
		return map;
	}

	@Override
	@Transactional(readOnly=true)
	public BossPaginationVo<Product> findPage(int pageNum,String name) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("name", name);
		BossPaginationVo<Product> page = new BossPaginationVo<Product>();
		page.setCurrentPage(pageNum);
		int total = productMapper.getCount(paramMap);
		if(total <= 0){
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<Product> dataList = productMapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	@Transactional(readOnly=true)
	public Product findById(int id) {
		return productMapper.findById(id);
	}

	@Override
	public boolean disabled(int id, int flag) {
		try {
			productMapper.updateStatus(id, flag);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<Product> getProductListByComboId(int comboId) {
		return productMapper.getProductListByComboId(comboId);
	}

	@Override
	public List<Product> getSelectProductList(String name) {
		return productMapper.getSelectProductList(name);
	}

}
