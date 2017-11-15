/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.common.settings.service;

import java.util.Map;

import com.huashi.common.settings.domain.HostWhiteList;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO ip白名单
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月23日 下午10:16:03
 */

public interface IHostWhiteListService {

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(HostWhiteList record);

	/**
	 * 
	 * TODO 删除 把delete_sign设置成1
	 * 
	 * @param id
	 * @return
	 */
	int deleteByPrimaryKey(int id);

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param record
	 * @return
	 */
	Map<String, Object> batchInsert(HostWhiteList record);

	/**
	 * 
	 * TODO查询用户记录 返回对象
	 * 
	 * @param id
	 * @return
	 */
	HostWhiteList getById(int id);

	/**
	 * 获取Ip记录(分页)
	 * 
	 * @param userId
	 *            用户编号
	 * @param ip
	 *            ip
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param currentPage
	 *            当前页码
	 * @return
	 */
	PaginationVo<HostWhiteList> findPage(int userId, String ip, String startDate, String endDate, String currentPage);

	/**
	 * boss 查询全部ip
	 * 
	 * @param pageNum
	 * @param ip
	 * @param status
	 * @param userId
	 * @return
	 */
	BossPaginationVo<HostWhiteList> findPageBoss(int pageNum, String ip, String status, String userId);

	/**
	 * 
	 * TODO 服务器IP是否允许通过
	 * 
	 * @param userId
	 * @param ip
	 * @return
	 */
	boolean ipAllowedPass(int userId, String ip);

	/**
	 * 
	 * TODO 重新载入REDIS
	 * 
	 * @return
	 */
	boolean reloadToRedis();

}
