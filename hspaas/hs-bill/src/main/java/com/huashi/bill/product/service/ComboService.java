package com.huashi.bill.product.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.bill.product.dao.ComboMapper;
import com.huashi.bill.product.dao.ComboProductMapper;
import com.huashi.bill.product.domain.Combo;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * 套餐
 * @author ym
 * @created_at 2016年7月12日下午5:43:54
 */
@Service
public class ComboService implements IComboService {
	
	
	@Autowired
	private ComboProductMapper comboProductMapper;
	@Autowired
	private ComboMapper comboMapper;

	@Override
	@Transactional(readOnly=false)
	public boolean create(Combo combo,String productIds) {
		try {
			comboMapper.insert(combo);
			Map<String, Object> paramMap = new HashMap<String,Object>();
			paramMap.put("comboId", combo.getId());
			String[] ids = productIds.split(",");
			for(String productId : ids){
				paramMap.put("productId", Integer.valueOf(productId));
				comboProductMapper.insert(paramMap);
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		return true;
	}

	@Override
	@Transactional(readOnly=false)
	public boolean update(Combo combo,String productIds) {
		try {
			comboMapper.update(combo);
			comboProductMapper.deleteByComboId(combo.getId());
			Map<String, Object> paramMap = new HashMap<String,Object>();
			paramMap.put("comboId", combo.getId());
			String[] ids = productIds.split(",");
			for(String productId : ids){
				paramMap.put("productId", Integer.valueOf(productId));
				comboProductMapper.insert(paramMap);
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		return true;
	}

	@Override
	@Transactional(readOnly=false)
	public boolean delete(int id) {
		try {
			comboMapper.delete(id);
			comboProductMapper.deleteByComboId(id);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		return true;
	}

	@Override
	public BossPaginationVo<Combo> findPage(int pageNum,String name,String start,String end) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("name", name);
		paramMap.put("startTime", start);
		paramMap.put("endTime", end);
		BossPaginationVo<Combo> page = new BossPaginationVo<Combo>();
		page.setCurrentPage(pageNum);
		int total = comboMapper.getCount(paramMap);
		if(total <= 0){
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<Combo> dataList = comboMapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}


	@Override
	public Combo findById(int id) {
		return comboMapper.findById(id);
	}

	/* (non-Javadoc)
	 * @see com.huashi.common.product.service.IComboService#updateStatus(int, int)
	 */
	@Override
	public boolean updateStatus(int id, int status) {
		try {
			comboMapper.updateStatus(id, status);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public PaginationVo<Combo> findPageList(String currentPage) {

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params =  new HashMap<String,Object>();
		int totalRecord = comboMapper.getCountNum(params);
		if (totalRecord == 0) {
            {
                return null;
            }
        }
		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<Combo> list = comboMapper.findPageList(params);
		if (list == null || list.isEmpty()) {
            {
                return null;
            }
        }
		return new PaginationVo<Combo>(list, _currentPage, totalRecord);
	}

	@Override
	public List<Combo> findLatestCombo(int article) {
		if(article<=0){
			article=5;
		}
		Map<String, Object> params =  new HashMap<String,Object>();
		params.put("pageRecord", article);
		params.put("startPage", 0);
		return comboMapper.findPageList(params);
	}

}
