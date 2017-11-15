package com.huashi.sms.passage.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.domain.SmsPassageControl;

/**
 * 
 * TODO 通道控制表
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年10月10日 下午4:37:45
 */
public interface ISmsPassageControlService {
	/**
	 * 保存通道控制
	 * 
	 * @param control
	 * @return 1成功 0失败 2重复 3重复记录为停用
	 */
	int save(SmsPassageControl control);

	/**
	 * 更新通道控制
	 * 
	 * @param control
	 * @return 1成功 0失败 2重复 3重复记录为停用
	 */
	int update(SmsPassageControl control);
	
	/**
	 * 更新状态
	 * @param control
	 * @return
	 */
	boolean updateStatus(SmsPassageControl control);

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	boolean deleteById(int id);
	
	/**
	 * 根据id获取渠道控制信息
	 * @param id
	 * @return
	 */
	SmsPassageControl get(int id);

	/**
	 * 
	 * TODO 后台查询全部短信通道控制 分页
	 * 
	 * @param pageNum
	 * @param keyword
	 * @param status
	 * @return
	 */
	BossPaginationVo<SmsPassageControl> findPage(int pageNum, String keyword, String status);
}
