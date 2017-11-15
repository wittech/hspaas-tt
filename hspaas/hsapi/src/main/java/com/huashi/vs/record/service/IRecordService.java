/**
 * 
 */
package com.huashi.vs.record.service;

import java.util.List;

import com.huashi.common.vo.PaginationVo;
import com.huashi.vs.record.domain.Record;

/**
 * 语音发送记录服务接口类
 * 
 * @author Administrator
 *
 */
public interface IRecordService {

	/**
	 * 语音验证码发送
	 * 
	 * @param record
	 * @return
	 */
	boolean send(Record record);

	/**
	 * 根据id获取对象信息
	 * 
	 * @param id
	 * @return
	 */
	Record selectByPrimaryKey(Long id);

	/**
	 * 查询全部发送记录
	 * 
	 * @param userId
	 *            登陆id
	 * @param phoneNumber
	 *            手机号码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param currentPage
	 *            当前页码
	 * @param status
	 *            状态
	 * @param type
	 *            类型（0语音验证码 1 语音通知）
	 * @return
	 */
	PaginationVo<Record> findPage(int userId, String phoneNumber, String startDate, String endDate,
			String currentPage, String status, String type);
	
	/**
	 * 根据手机号码 当前用户id 获取记录信息
	 * @param userid
	 * @param mobile
	 * @return
	 */
	List<Record> findMobileRecord(int userid,String mobile);
}
