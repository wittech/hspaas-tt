
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.common.notice.service;

import java.util.List;

import com.huashi.common.notice.domain.NotificationMessage;
import com.huashi.common.settings.context.SettingsContext.NotificationMessageTemplateType;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO 通知消息服务接口类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:08:47
 */

public interface INotificationMessageService {

	/**
	 * 
	 * TODO 前台用户查询私信消息（分页）
	 * 
	 * @param userId
	 * @param currentPage
	 * @return
	 */
	PaginationVo<NotificationMessage> findUserPage(int userId, String currentPage,int status);

	/**
	 * 
	 * TODO 获取未读私信信息
	 * 
	 * @param userId
	 * @return
	 */
	List<NotificationMessage> findUnReadList(int userId);

	/**
	 * 
	 * TODO 创建私信消息
	 * 
	 * @param message
	 * @return
	 */
	boolean create(NotificationMessage message);
	
	/**
	 * 
	 * TODO 创建私信消息
	 * 
	 * @param message
	 * @return
	 */
	boolean save(int userId, NotificationMessageTemplateType type, String content);

	boolean update(NotificationMessage message);

	boolean delete(int id);

	/**
	 * 
	 * TODO 后台查询私信信息
	 * 
	 * @param pageNum
	 * @param keyword
	 * @return
	 */
	BossPaginationVo<NotificationMessage> findPage(int pageNum, String keyword);

	NotificationMessage findById(int id);
	
	/**
	 * 
	   * TODO 更新用户的所有未读至已读
	   * @param userId
	   * @return
	 */
	boolean updateToRead(int userId);

}
