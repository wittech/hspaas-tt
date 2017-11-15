
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.notice.context.MessageContext.ReadStatus;
import com.huashi.common.notice.dao.NotificationMessageMapper;
import com.huashi.common.notice.domain.NotificationMessage;
import com.huashi.common.settings.context.SettingsContext.NotificationMessageTemplateType;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO 消息通知服务接口实现类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:11:05
 */
@Service
public class NotificationMessageService implements INotificationMessageService {

	@Autowired
	private NotificationMessageMapper notificationMessageMapper;

	@Override
	public PaginationVo<NotificationMessage> findUserPage(int userId, String currentPage,int status) {
		if (userId == 0)
			return null;

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params =  new HashMap<String,Object>();
		params.put("userId", userId);
		if(status>=0){
			params.put("status", status);
		}
		int totalRecord = notificationMessageMapper.getCountByUserId(params);
		if (totalRecord == 0)
			return null;

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);
		List<NotificationMessage> list = notificationMessageMapper.findPageListByUserId(params);
		if (list == null || list.isEmpty())
			return null;

		return new PaginationVo<NotificationMessage>(list, _currentPage, totalRecord);
	}

	@Override
	public boolean create(NotificationMessage message) {
		try {
			message.setCreateTime(new Date());
			notificationMessageMapper.insertSelective(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean update(NotificationMessage message) {
		try {
			notificationMessageMapper.updateByPrimaryKeySelective(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean delete(int id) {
		try {
			notificationMessageMapper.deleteByPrimaryKey(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public BossPaginationVo<NotificationMessage> findPage(int pageNum, String keyword) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		BossPaginationVo<NotificationMessage> page = new BossPaginationVo<NotificationMessage>();
		page.setCurrentPage(pageNum);
		paramMap.put("keyword", keyword);
		int total = notificationMessageMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<NotificationMessage> dataList = notificationMessageMapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	public NotificationMessage findById(int id) {
		return notificationMessageMapper.findById(id);
	}

	@Override
	public List<NotificationMessage> findUnReadList(int userId) {
		if (userId == 0)
			return null;

		return notificationMessageMapper.findUnReadByUserId(userId);
	}

	@Override
	public boolean updateToRead(int userId) {
		if(userId == 0)
			return false;
		try {
			notificationMessageMapper.updateToRead(userId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean save(int userId, NotificationMessageTemplateType type, String content) {
		NotificationMessage message = new NotificationMessage();
		message.setUserId(userId);
		message.setStatus(ReadStatus.UNREAD.getCode());
		message.setType(type.getCode());
		message.setTitle(type.getTitle());
		message.setContent(content);
		message.setCreateTime(new Date());
		return notificationMessageMapper.insert(message) > 0;
	}

}
