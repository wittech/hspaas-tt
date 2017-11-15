/**
 * 
 */
package com.huashi.sms.record.service;

import java.util.List;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.record.domain.SmsMoMessageReceive;

/**
 * 短信接收记录服务接口类
 * 
 * @author Administrator
 *
 */
public interface ISmsMoMessageService {
	/**
	 * 获取短信接收记录(分页)
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
	PaginationVo<SmsMoMessageReceive> findPage(int userId, String phoneNumber, String startDate, String endDate,
			String currentPage);

	BossPaginationVo<SmsMoMessageReceive> findPage(int pageNum,String keyword);
	
	/**
     * 
       * TODO 完成上行回复逻辑
       * 
       * @param list
       * @return
     */
    int doFinishReceive(List<SmsMoMessageReceive> list);
    
    /**
     * 
       * TODO 批量插入信息
       * 
       * @param list
       * @return
     */
    int batchInsert(List<SmsMoMessageReceive> list);
    
    /**
     * 
       * TODO 发送错误信息值异常回执数据
       * 
       * @param obj
       * @return
     */
    boolean doReceiveToException(Object obj);
    
}
