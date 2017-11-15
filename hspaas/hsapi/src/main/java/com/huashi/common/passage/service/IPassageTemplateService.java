//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月25日       杨猛　     新建
//*********************************************************************
package com.huashi.common.passage.service;

import java.util.List;

import com.huashi.common.passage.domain.PassageTemplate;
import com.huashi.common.vo.BossPaginationVo;

/**
 * @author ym
 * @created_at 2016年8月25日下午4:22:00
 */
public interface IPassageTemplateService {

	
	public boolean create(PassageTemplate template);
	
	public boolean update(PassageTemplate template);
	
	public BossPaginationVo<PassageTemplate> findPage(int pageNum,String keyword,int type);
	
	public PassageTemplate findById(int id);
	
	public boolean deleteById(int id);
	
	public List<PassageTemplate> findListByType(int type);
}
