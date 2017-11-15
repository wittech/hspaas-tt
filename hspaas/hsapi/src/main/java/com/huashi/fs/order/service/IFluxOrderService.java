/**
 * 
 */
package com.huashi.fs.order.service;

import java.util.List;

import com.huashi.common.vo.PaginationVo;
import com.huashi.fs.order.domain.FluxOrder;

/**
 * 
 * TODO 流量订单服务接口类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年8月29日 下午5:54:35
 */
public interface IFluxOrderService {
	/**
	 * 获取客户订单记录(分页)
	 * 
	 * @param userId
	 *            客户编号
	 * @param mobile
	 *            手机号码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param currentPage
	 *            当前页码
	 * @return
	 */
	PaginationVo<FluxOrder> findPage(int userId, String mobile, String startDate, String endDate, String currentPage);

	/**
	 * 根据用户id 和手机号码查询最新数据记录
	 * 
	 * @param userId
	 * @param mobile
	 * @return
	 */
	List<FluxOrder> findUserIdAndMobileRecord(int userId, String mobile);

}
