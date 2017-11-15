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

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.finance.dao.InvoiceBalanceMapper;
import com.huashi.common.finance.domain.InvoiceBalance;

/**
 * TODO 发票余额接口实现类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午9:35:24
 */
@Service
public class InvoiceBalanceService implements IInvoiceBalanceService {

	@Autowired
	private InvoiceBalanceMapper invoiceBalanceMapper;

	@Override
	public InvoiceBalance getByUserId(int userId) {
		return invoiceBalanceMapper.getByUserId(userId);
	}

	@Override
	public boolean save(int userId) {
		InvoiceBalance balance = new InvoiceBalance();
		balance.setUserId(userId);
		balance.setMoney(0d);
		balance.setCreateTime(new Date());
		return invoiceBalanceMapper.insert(balance) > 0;
	}

	@Override
	public boolean updateAvaiableBalance(int userId, Double money) {
		InvoiceBalance invoiceBalance =getByUserId(userId);
		
		invoiceBalance.setModifyTime(new Date());
		invoiceBalance.setModifyTimes(invoiceBalance.getModifyTimes() + 1);
		// 如果金额为负数,则自动减法
		invoiceBalance.setMoney(invoiceBalance.getMoney() + money);
		
		return invoiceBalanceMapper.updateByPrimaryKeySelective(invoiceBalance) > 0;
	}

}
