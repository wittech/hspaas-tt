//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月25日       杨猛　     新建
//*********************************************************************
package com.huashi.sms.passage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.dao.SmsPassageGroupDetailMapper;
import com.huashi.sms.passage.dao.SmsPassageGroupMapper;
import com.huashi.sms.passage.domain.SmsPassageGroup;
import com.huashi.sms.passage.domain.SmsPassageGroupDetail;

/**
 * 短信通道组
 * 
 * @author ym
 * @created_at 2016年8月25日下午6:13:37
 */
@Service
public class SmsPassageGroupService implements ISmsPassageGroupService {

	@Autowired
	private SmsPassageGroupMapper mapper;
	@Autowired
	private SmsPassageGroupDetailMapper detailMapper;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Transactional(readOnly = false)
	public boolean create(SmsPassageGroup group) {
		try {
			mapper.insert(group);
			int priority = 1;
			for (SmsPassageGroupDetail passage : group.getDetailList()) {
				passage.setPriority(priority);
				passage.setGroupId(group.getId());
				detailMapper.insert(passage);
				priority++;
			}
			return true;
		} catch (Exception e) {
			logger.error("添加通道组失败", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean update(SmsPassageGroup group) {
		try {
			mapper.updateByPrimaryKeySelective(group);
			int priority = 1;
			detailMapper.deleteByGroupId(group.getId().intValue());
			for (SmsPassageGroupDetail passage : group.getDetailList()) {
				passage.setPriority(priority);
				passage.setGroupId(group.getId());
				detailMapper.insert(passage);
				priority++;
			}
			return true;
		} catch (Exception e) {
			logger.error("修改通道组失败", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean deleteById(int id) {
		try {
			mapper.deleteByPrimaryKey(id);
			detailMapper.deleteByGroupId(id);
		} catch (Exception e) {
			logger.error("删除通道组失败", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return false;
	}

	@Override
	public BossPaginationVo<SmsPassageGroup> findPage(int pageNum, String keyword) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("keyword", keyword);
		BossPaginationVo<SmsPassageGroup> page = new BossPaginationVo<SmsPassageGroup>();
		page.setCurrentPage(pageNum);
		int total = mapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsPassageGroup> dataList = mapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	public SmsPassageGroup findById(int id) {
		SmsPassageGroup group = mapper.selectByPrimaryKey(id);
		List<SmsPassageGroupDetail> passageList = detailMapper.findPassageByGroupId(id);
		group.getDetailList().addAll(passageList);
		return group;
	}

	@Override
	public List<SmsPassageGroup> findAll() {
		return mapper.findAll();
	}

	@Override
	public List<SmsPassageGroupDetail> findPassageByGroupId(int groupId) {
		return detailMapper.findPassageByGroupId(groupId);
	}

	@Override
	public boolean doChangeGroupPassage(int groupId, int passageId) {
		try {
			return detailMapper.updateGroupPassageId(groupId, passageId) > 0;
		} catch (Exception e) {
			logger.error("切换通道组中通道出错", e);
			return false;
		}
		
	}

}
