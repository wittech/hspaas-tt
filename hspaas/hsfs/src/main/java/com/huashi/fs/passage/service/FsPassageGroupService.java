//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月27日       杨猛　     新建
//*********************************************************************
package com.huashi.fs.passage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.fs.passage.dao.FluxPassageGroupMapper;
import com.huashi.fs.passage.domain.FluxPassageGroup;

/**
 * @author ym
 * @created_at 2016年8月27日下午9:43:28
 */
public class FsPassageGroupService implements IFsPassageGroupService {
	
	@Autowired
	private FluxPassageGroupMapper mapper;

	@Override
	public boolean create(FluxPassageGroup group) {
		return false;
	}

	@Override
	public boolean update(FluxPassageGroup group) {
		return false;
	}

	@Override
	public boolean deleteById(int id) {
		return mapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public BossPaginationVo<FluxPassageGroup> findPage(int pageNum, String keyword) {
		return null;
	}

	@Override
	public FluxPassageGroup findById(int id) {
		return null;
	}

	@Override
	public List<FluxPassageGroup> findAll() {
		return null;
	}

}
