/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.sms.settings.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.settings.domain.SmsMobileBlackList;

/**
 * TODO 黑名单设置
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月20日 下午12:17:28
 */

public interface ISmsMobileBlackListService {
	/**
	 * 
	 * TODO 批量设置黑名单 分隔符以换行符 分割
	 * 
	 * @param black
	 * @return
	 */
	Map<String, Object> batchInsert(SmsMobileBlackList black);

	/**
	 * 获取黑名单记录(分页)
	 * 
	 * @param userId
	 *            用户编号
	 * @param phoneNumber
	 *            手机号码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param currentPage
	 *            当前页码
	 * @return
	 */
	PaginationVo<SmsMobileBlackList> findPage(String mobile, String startDate, String endDate,
			String currentPage);

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param id
	 * @return
	 */
	int deleteByPrimaryKey(int id);

	/**
	 * 
	 * TODO 手机号码是否为黑名单
	 * 
	 * @param mobile
	 * @return
	 */
	boolean isMobileBelongtoBlacklist(String mobile);

	/**
	 * 
	 * TODO 查找全部的黑名单
	 * 
	 * @return
	 */
	List<String> findAll();

	/**
	 * 
	 * TODO 过滤号码中的所有黑名单手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	List<String> filterBlacklistMobile(List<String> mobiles);

	BossPaginationVo<SmsMobileBlackList> findPage(int pageNum, String keyword);
	
	/**
	 * 
	   * TODO 加载到REDIS
	   * @return
	 */
	boolean reloadToRedis();
}
