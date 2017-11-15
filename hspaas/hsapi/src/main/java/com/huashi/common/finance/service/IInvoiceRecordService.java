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

import com.huashi.common.finance.domain.InvoiceRecord;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO 发票管理服务接口类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午8:55:41
 */

public interface IInvoiceRecordService {

	/**
	 * 
	 * TODO 开票
	 * 
	 * @param balanceRecord
	 * @return
	 */
	boolean save(InvoiceRecord balanceRecord);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param userId
	 * @param currentPage
	 * @return
	 */
	PaginationVo<InvoiceRecord> findPage(int userId, String currentPage);

	boolean update(InvoiceRecord record);

	boolean delete(int id);

	InvoiceRecord findById(int id);

	BossPaginationVo<InvoiceRecord> findPage(int pageNum,
			String invoiceKeyword, String userKeyword);

}
