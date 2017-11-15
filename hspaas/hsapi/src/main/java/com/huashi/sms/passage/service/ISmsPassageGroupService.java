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

import java.util.List;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.domain.SmsPassageGroup;
import com.huashi.sms.passage.domain.SmsPassageGroupDetail;

/**
 * @author ym
 * @created_at 2016年8月25日下午5:22:42
 */
public interface ISmsPassageGroupService {

	boolean create(SmsPassageGroup group);

	boolean update(SmsPassageGroup group);

	boolean deleteById(int id);

	BossPaginationVo<SmsPassageGroup> findPage(int pageNum, String keyword);

	SmsPassageGroup findById(int id);

	List<SmsPassageGroup> findAll();

	List<SmsPassageGroupDetail> findPassageByGroupId(int groupId);

	/**
	 * 将通道组下面的所有通道切换值参数通道
	 * 
	 * @param passageId
	 * @return
	 */
	boolean doChangeGroupPassage(int groupId, int passageId);
}
