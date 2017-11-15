/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.common.finance.service;

import com.huashi.common.finance.domain.InvoiceBalance;

/**
 * TODO 发票余额
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午9:34:15
 */

public interface IInvoiceBalanceService {
	/**
	 * 
	 * TODO 根据用户获取可开发票余额
	 * 
	 * @param userId
	 * @return
	 */
	InvoiceBalance getByUserId(int userId);

	/**
	 * 
	 * TODO 根据用户ID插入用户余额配置信息
	 * 
	 * @param userId
	 * @return
	 */
	boolean save(int userId);

	/**
	 * 
	 * TODO 更新可用发票余额信息
	 * 
	 * @param userId
	 *            用户编号
	 * @param money
	 *            金额，可为负数，如果为负数则做减法操作
	 * @return
	 */
	boolean updateAvaiableBalance(int userId, Double money);
}
