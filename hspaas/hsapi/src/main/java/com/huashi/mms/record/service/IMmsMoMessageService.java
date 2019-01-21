/**
 * 
 */
package com.huashi.mms.record.service;

import java.util.List;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.mms.record.domain.MmsMoMessageReceive;

/**
 * TODO 彩信接收记录服务接口类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 上午11:04:00
 */
public interface IMmsMoMessageService {

    /**
     * 获取彩信接收记录(分页)
     * 
     * @param userId 用户编号
     * @param phoneNumber 手机号码
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param currentPage 当前页码
     * @return
     */
    PaginationVo<MmsMoMessageReceive> findPage(int userId, String phoneNumber, String startDate, String endDate,
                                               String currentPage);

    BossPaginationVo<MmsMoMessageReceive> findPage(int pageNum, String keyword);

    /**
     * TODO 完成上行回复逻辑
     * 
     * @param list
     * @return
     */
    int doFinishReceive(List<MmsMoMessageReceive> list);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<MmsMoMessageReceive> list);

    /**
     * TODO 发送错误信息值异常回执数据
     * 
     * @param obj
     * @return
     */
    boolean doReceiveToException(Object obj);

}
