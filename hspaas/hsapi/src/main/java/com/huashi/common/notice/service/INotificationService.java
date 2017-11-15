package com.huashi.common.notice.service;

import java.util.List;

import com.huashi.common.notice.domain.Notification;
import com.huashi.common.vo.BossPaginationVo;

public interface INotificationService {

	/**
	 * 
	   * TODO 查询前4条公告（按照发布时间倒叙）
	   * @return
	 */
	List<Notification> findTopList();
	
	boolean create(Notification record);
	
	boolean update(Notification record);
	
	boolean delete(int id);
	
	Notification findById(int id);
	
	BossPaginationVo<Notification> findPage(int pageNum);
}
