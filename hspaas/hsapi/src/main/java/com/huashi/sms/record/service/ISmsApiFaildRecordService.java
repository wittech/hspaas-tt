/**
 *
 */
package com.huashi.sms.record.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.record.domain.SmsApiFailedRecord;

/**
 * 短信错误记录服务接口类
 *
 * @author tenx
 */
public interface ISmsApiFaildRecordService {

    /**
     * TODO 获取短信错误记录(分页)
     *
     * @param userId
     * @param mobile
     * @param startDate
     * @param endDate
     * @param currentPage
     * @return
     */
    PaginationVo<SmsApiFailedRecord> findPage(int userId, String mobile, String startDate, String endDate, String currentPage);

    /**
     * TODO 添加错误记录
     *
     * @param record
     * @return
     */
    boolean save(SmsApiFailedRecord record);

    /**
     * TODO 查询API调用错误记录信息
     *
     * @param pageNum
     * @param keyword
     * @return
     */
    BossPaginationVo<SmsApiFailedRecord> findPage(int pageNum, String keyword);
}
