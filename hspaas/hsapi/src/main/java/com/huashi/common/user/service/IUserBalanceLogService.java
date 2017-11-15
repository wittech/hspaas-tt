package com.huashi.common.user.service;

import com.huashi.common.user.domain.UserBalanceLog;
import com.huashi.common.vo.BossPaginationVo;

/**
 * 用户余额日志服务
 * @author Administrator
 *
 */
public interface IUserBalanceLogService {
	/**
	 * 查询全部余额信息
	 * @param pageNum
	 * @param userId
	 * @param platformType
	 * @return
	 */
	BossPaginationVo<UserBalanceLog> findPageBoss(int pageNum, String userId,String platformType);
}
