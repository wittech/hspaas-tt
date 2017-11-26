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

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.settings.domain.SmsMobileWhiteList;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO 白名单服务接口类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月20日 下午12:18:39
 */

public interface ISmsMobileWhiteListService {
	/**
	 * 
	 * TODO 批量设置白名单 分隔符以换行符 分割
	 * 
	 * @param white
	 * @return
	 */
	Map<String, Object> batchInsert(SmsMobileWhiteList white);

	/**
	 * 
	 * TODO 查询
	 * 
	 * @param userId
	 * @return
	 */
	List<SmsMobileWhiteList> selectByUserId(int userId);

	/**
	 * 获取白名单记录(分页)
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
	PaginationVo<SmsMobileWhiteList> findPage(int userId, String phoneNumber,
			String startDate, String endDate, String currentPage);

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param id
	 * @return
	 */
	int deleteByPrimaryKey(int id);

	BossPaginationVo<SmsMobileWhiteList> findPage(int pageNum, String keyword);
	
	/**
	 * 
	   * TODO 加载到REDIS
	   * @return
	 */
	boolean reloadToRedis();
	
	/**
	 * 
	   * TODO 是否是报备白名单手机号码
	   * @param userId
	   * @param mobile
	   * @return
	 */
	boolean isMobileWhitelist(int userId, String mobile);
	
	/**
	 * 
	   * TODO 根据用户ID获取白名单手机号码集合
	   * 
	   * @param userId
	   * @return
	 */
	Set<String> getByUserId(int userId);
}
