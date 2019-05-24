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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.finance.dao.InvoiceBalanceMapper;
import com.huashi.common.finance.dao.InvoiceRecordMapper;
import com.huashi.common.finance.domain.InvoiceBalance;
import com.huashi.common.finance.domain.InvoiceRecord;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO 发票管理服务接口实现类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午9:00:49
 */

@Service
public class InvoiceRecordService implements IInvoiceRecordService {

    @Autowired
    private InvoiceRecordMapper balanceRecordMapper;
    @Autowired
    private InvoiceBalanceMapper invoiceBalanceMapper;

    @Override
    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public boolean save(InvoiceRecord balanceRecord) {
        try {
            balanceRecord.setCreateTime(new Date());
            if (balanceRecord.isNeedUpdateBalance()) {
                InvoiceBalance balance = invoiceBalanceMapper.getByUserId(balanceRecord.getUserId());
                if (balance == null) {
                    return false;
                }

                if (balance.getMoney() == 0) {
                    return false;
                }

                if (balance.getMoney() < balanceRecord.getMoney()) {
                    return false;
                }
                balance.setMoney(balance.getMoney() - balanceRecord.getMoney());
                invoiceBalanceMapper.updateByPrimaryKeySelective(balance);
            }
            return balanceRecordMapper.insertSelective(balanceRecord) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public PaginationVo<InvoiceRecord> findPage(int userId, String currentPage) {
        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        int totalRecord = balanceRecordMapper.getCountByUserId(params);
        if (totalRecord == 0) {
            {
                return null;
            }
        }

        params.put("startPage", PaginationVo.getStartPage(_currentPage));
        params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<InvoiceRecord> list = balanceRecordMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            {
                return null;
            }
        }
        return new PaginationVo<InvoiceRecord>(list, _currentPage, totalRecord);
    }

    @Override
    public boolean update(InvoiceRecord record) {
        try {
            balanceRecordMapper.updateByPrimaryKeySelective(record);
            if (record.getStatus() == 2) {
                InvoiceBalance balance = invoiceBalanceMapper.getByUserId(record.getUserId());
                balance.setMoney(balance.getMoney() - record.getMoney());
                balance.setModifyTime(new Date());
                balance.setModifyTimes(balance.getModifyTimes() + 1);
                invoiceBalanceMapper.updateByPrimaryKeySelective(balance);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try {
            balanceRecordMapper.deleteByPrimaryKey(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public InvoiceRecord findById(int id) {
        return balanceRecordMapper.findById(id);
    }

    @Override
    public BossPaginationVo<InvoiceRecord> findPage(int pageNum, String invoiceKeyword, String userKeyword) {
        BossPaginationVo<InvoiceRecord> page = new BossPaginationVo<InvoiceRecord>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("invoiceKeyword", invoiceKeyword);
        paramMap.put("userKeyword", userKeyword);
        int count = balanceRecordMapper.findCount(paramMap);
        page.setCurrentPage(pageNum);
        page.setTotalCount(count);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<InvoiceRecord> list = balanceRecordMapper.findList(paramMap);
        page.setList(list);
        return page;
    }

}
