package com.huashi.sms.report.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.user.model.UserModel;
import com.huashi.sms.report.domain.SmsSubmitHourReport;
import com.huashi.sms.report.model.ChinessCmcpSubmitReportVo;

/**
 * TODO 提交数据统计报表接口
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年7月20日 下午10:00:06
 */
public interface ISmsSubmitHourReportService {

    /**
     * TODO 生成小时提交报表数据（离线定时跑）
     * 
     * @return
     */
    int beBornSubmitHourReport();

    /**
     * TODO 生成小时提交报表数据, 按照固定小时数
     * 
     * @param hours
     * @return
     */
    int beBornSubmitHourReport(int hours);

    /**
     * TODO 查询用户通道短信通道提交统计报告（即用户和通道两个维度）
     * 
     * @param userId 用户ID(为空则查询全部))
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return
     */
    Map<UserModel, List<SmsSubmitHourReport>> findUserPassageSubmitReport(Integer userId, String startDate,
                                                                          String endDate);

    /**
     * TODO 查询用户短信提交统计报告
     * 
     * @param userId 用户ID(为空则查询全部))
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return
     */
    List<SmsSubmitHourReport> findUserSubmitReport(Integer userId, String startDate, String endDate);

    /**
     * TODO 获取通道提交统计数据
     * 
     * @param passageId 通道 ID
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return
     */
    List<SmsSubmitHourReport> findPassageSubmitReport(Integer passageId, String startDate, String endDate);

    /**
     * TODO 获取省份通道提交统计数据
     * 
     * @param passageId 通道 ID
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return
     */
    List<SmsSubmitHourReport> findProvinceSubmitReport(Integer passageId, String startDate, String endDate);

    /**
     * TODO 获取全国运营商统计报告
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    ChinessCmcpSubmitReportVo getCmcpSubmitReport(String startDate, String endDate);

}
