//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月25日       杨猛　     新建
//*********************************************************************
package com.huashi.sms.passage.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageProvince;

/**
 * @author ym
 * @created_at 2016年8月25日下午5:22:24
 */
public interface ISmsPassageService {

	/**
	 * 添加通道
	 * 
	 * @param passage
	 * @param provinceCodes
	 * @return
	 */
	Map<String, Object> create(SmsPassage passage, String provinceCodes);

	/**
	 * 修改通道
	 * 
	 * @param passage
	 * @param provinceCodes
	 * @return
	 */
	Map<String, Object> update(SmsPassage passage, String provinceCodes);

	/**
	 * 删除通道
	 * 
	 * @param id
	 * @return
	 */
	boolean deleteById(int id);

	BossPaginationVo<SmsPassage> findPage(int pageNum, String keyword);

	List<SmsPassage> findAll();

	SmsPassage findById(int id);

	/**
	 * TODO 根据通道组ID获取所有通道信息
	 *
	 * @param groupId
	 * @return
	 */
	List<SmsPassage> findByGroupId(int groupId);

	/**
	 * TODO 获取最好的可用通道信息，结合优先级，通道状态等因素
	 *
	 * @param groupId
	 * @return
	 */
	SmsPassage getBestAvaiable(int groupId);

	/**
	 * 
	 * TODO 禁用或激活通道
	 * 
	 * @param id
	 * 		通道ID
	 * @param flag
	 * 		状态标识
	 * @return
	 */
	boolean disabledOrActive(int id, int flag);

	/**
	 * 
	 * TODO 根据运营商查询通道信息
	 * 
	 * @param cmcp
	 * @return
	 */
	List<SmsPassage> getByCmcp(int cmcp);

	/**
	 * 根据运营商、路由类型、状态查询全部可用通道组下面的通道
	 * 
	 * @param groupId
	 *            通道组id
	 * @param cmcp
	 *            运营商
	 * @param routeType
	 *            路由类型
	 * @return
	 */
	List<SmsPassage> findAccessPassages(int groupId, int cmcp, int routeType);

	List<SmsPassage> findByCmcpOrAll(int cmcp);

	/**
	 * 
	 * TODO 加载到REDIS
	 * 
	 * @return
	 */
	boolean reloadToRedis();

	/**
	 * 根据通道ID获取省份通道信息
	 * 
	 * @param passageId
	 * @return
	 */
	List<SmsPassageProvince> getPassageProvinceById(Integer passageId);

	/**
	 * 根据省份代码和运营商获取通道信息
	 * 
	 * @param provinceCode
	 * @param cmcp
	 * @return
	 */
	List<SmsPassage> getByProvinceAndCmcp(Integer provinceCode, int cmcp);

	/**
	 * 测试通道
	 * 
	 * @param passageId
	 *            当前测试通道ID
	 * @param mobile
	 *            测试手机号码（支持多个）
	 * @param content
	 *            短信内容
	 * @return
	 */
	boolean doTestPassage(Integer passageId, String mobile, String content);

	/**
	 * 
	 * TODO 查询所有有效的通道代码信息
	 * 
	 * @return
	 */
	List<String> findPassageCodes();

	/**
	 * 
	   * TODO 监控告警短信(专指发送配置系统配置中的告警手机号码，内部使用)
	   * 
	   * @param mobile
	   * @param content
	   * @return
	 */
	boolean doMonitorSmsSend(String mobile, String content);
	
	/**
	 * 
	   * TODO 获取通道消息队列消费者数量
	   * 
	   * @param protocol
	   * 		协议类型
	   * @param passageCode
	   * 		通道代码
	   * @return
	 */
	boolean isPassageBelongtoDirect(String protocol, String passageCode);
	
}
