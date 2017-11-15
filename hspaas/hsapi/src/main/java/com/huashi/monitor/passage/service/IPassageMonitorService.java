package com.huashi.monitor.passage.service;

import java.util.List;
import java.util.Map;

import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.model.PassageReachRateReport;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;

/**
 * 
 * TODO 监管中通道服务接口
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年2月23日 下午1:50:43
 */
public interface IPassageMonitorService {

	/**
	 * 
	 * TODO 查询轮训通道信息
	 * 
	 * @return
	 */
	List<PassagePullReport> findPassagePullReport();

	/**
	 * 
	 * TODO 开启通道数据抓取
	 * 
	 * @return
	 */
	boolean startPassagePull();

	/**
	 * 
	 * TODO 加入轮训通道
	 * 
	 * @param access
	 * @return
	 */
	boolean addPassagePull(SmsPassageAccess access);

	/**
	 * 
	 * TODO 移除轮训通道
	 * 
	 * @param access
	 * @return
	 */
	boolean removePasagePull(SmsPassageAccess access);

	/**
	 * 
	 * TODO 更新通道轮训报告数据
	 * 
	 * @param report
	 * @return
	 */
	boolean updatePullReport(PassagePullReport report);

	/**
	 * 
	 * TODO 清除原有信息
	 */
	void flushPullReport();
	
	/**
	 * 
	   * TODO 更新数据轮训报告至REDIS
	   * 
	   * @param key
	   * @param report
	   * @return
	 */
	boolean updatePullReportToRedis(String key, PassagePullReport report);

	/**
	 * 
	   * TODO 添加通道进行监控
	   * 
	   * @param model
	   * @return
	 */
	boolean addSmsPassageMonitor(SmsPassageReachrateSettings model);
	
	/**
	 * 
	   * TODO 获取通道到达率统计数据
	   * 
	   * @param passageId
	   * @return
	 */
	Map<Integer, List<PassageReachRateReport>> findReachrateReport(Integer passageId);
}
