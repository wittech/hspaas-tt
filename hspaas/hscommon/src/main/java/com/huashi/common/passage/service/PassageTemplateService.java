package com.huashi.common.passage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.passage.dao.PassageTemplateDetailMapper;
import com.huashi.common.passage.dao.PassageTemplateMapper;
import com.huashi.common.passage.domain.PassageTemplate;
import com.huashi.common.passage.domain.PassageTemplateDetail;
import com.huashi.common.vo.BossPaginationVo;

/**
 * 通道模板业务处理
 * @author ym
 * @created_at 2016年8月25日下午4:21:09
 */
@Service
public class PassageTemplateService implements IPassageTemplateService{
	
	@Autowired
	private PassageTemplateMapper mapper;
	@Autowired
	private PassageTemplateDetailMapper detailMapper;
	

	@Override
	@Transactional(readOnly=false)
	public boolean create(PassageTemplate template) {
		try {
			mapper.insert(template);
			for(PassageTemplateDetail detail : template.getDetailList()){
				detail.setTemplateId(template.getId());
				detailMapper.insert(detail);
			}
			return true;
		} catch (Exception e) {	
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return false;
	}

	@Override
	@Transactional(readOnly=false)
	public boolean update(PassageTemplate template) {
		try {
			mapper.updateByPrimaryKeySelective(template);
			detailMapper.deleteByTemplateId(template.getId());
			for(PassageTemplateDetail detail : template.getDetailList()){
				detail.setTemplateId(template.getId());
				detailMapper.insert(detail);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return false;
	}

	@Override
	public BossPaginationVo<PassageTemplate> findPage(int pageNum, String keyword, int type) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("keyword", keyword);
		paramMap.put("type", type);
		BossPaginationVo<PassageTemplate> page = new BossPaginationVo<PassageTemplate>();
		page.setCurrentPage(pageNum);
		int total = mapper.findCount(paramMap);
		if(total <= 0){
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<PassageTemplate> dataList = mapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	public PassageTemplate findById(int id) {
		PassageTemplate template = mapper.selectByPrimaryKey(id);
		template.getDetailList().addAll(detailMapper.findListByTemplateId(id));
		return template;
	}

	@Override
	@Transactional(readOnly=false)
	public boolean deleteById(int id) {
		try {
			mapper.deleteByPrimaryKey(id);
			detailMapper.deleteByTemplateId(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return false;
	}

	@Override
	public List<PassageTemplate> findListByType(int type) {
        return mapper.findByPassageType(type);
	}

}
