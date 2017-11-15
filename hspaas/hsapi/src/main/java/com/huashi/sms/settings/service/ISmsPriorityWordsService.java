package com.huashi.sms.settings.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.settings.domain.SmsPriorityWords;

/**
 * 优先级词库配置服务
 * 
 * @author Administrator
 *
 */

public interface ISmsPriorityWordsService {
	/**
	 * 
	 * TODO 分页查询敏优先级词库配置（支撑平台）
	 * 
	 * @param pageNum
	 * @param keyword
	 * @return
	 */
	BossPaginationVo<SmsPriorityWords> findPage(int pageNum, String userId, String content);

	/**
	 * 根据id获取优先级敏感词库信息
	 * 
	 * @param id
	 * @return
	 */
	SmsPriorityWords get(int id);

	/**
	 * 修改/启用/禁用（支撑平台）
	 * 
	 * @param words
	 * @return
	 */
	boolean enableAndisable(SmsPriorityWords words);

	/**
	 * 保存
	 * 
	 * @param words
	 * @return
	 */
	boolean save(SmsPriorityWords words);

	/**
	 * 根据id删除
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(int id);
}
