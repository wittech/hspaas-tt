package com.huashi.sms.passage.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.domain.SmsPassageMessageTemplate;

/**
 * 
  * TODO 通道短信模板服务接口
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年8月30日 下午1:43:26
 */
public interface ISmsPassageMessageTemplateService {

	/**
	 * 
	   * TODO 添加通道短信模板
	   * 
	   * @param passageMessageTemplate
	   * @return
	 */
	boolean save(SmsPassageMessageTemplate passageMessageTemplate);

	/**
	 * 
	   * TODO 修改通道短信模板
	   * 
	   * @param passageMessageTemplate
	   * @return
	 */
	boolean update(SmsPassageMessageTemplate passageMessageTemplate);

	/**
	 * 删除通道短信模板
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	   * TODO 分页查询通道短信模板信息
	   * 
	   * @param params
	   * 	参数信息
	   * @return
	 */
	BossPaginationVo<SmsPassageMessageTemplate> findPage(Map<String, Object> params);

	/**
	 * 
	   * TODO 根据ID查询通道短信模板信息
	   * 
	   * @param id
	   * @return
	 */
	SmsPassageMessageTemplate get(Long id);

	/**
	 * 
	   * TODO 根据通道ID查询所有的通道短信模板信息
	   * 
	   * @param passageId
	   * @return
	 */
	List<SmsPassageMessageTemplate> findByPassageId(int passageId);

	/**
	 * 
	   * TODO 根据短信模板ID获取通道短信模板信息
	   * @param templateId
	   * @return
	 */
	SmsPassageMessageTemplate getByTemplateId(String templateId);
	
	/**
	 * 
	   * TODO 根据短信内容获取通道短信模板信息
	   * 
	   * @param passageId
	   * 		通道ID
	   * @param messageContent
	   * 		模板内容
	   * @return
	 */
	SmsPassageMessageTemplate getByMessageContent(Integer passageId, String messageContent);

	/**
	 * 
	 * TODO 禁用或激活通道
	 * 
	 * @param id
	 * 		通道短信模板ID
	 * @param status
	 * 		状态
	 * @return
	 */
	boolean disabledOrActive(Long id, int status);
	
	/**
	 * 
	 * TODO 判断输入内容是否符合模板内容
	 * 
	 * @param id
	 * @param content
	 * @return
	 */
	boolean isContentMatched(Long id, String content);

	/**
	 * 
	   * TODO 重新加载到REDIS
	   * 
	   * @return
	 */
	boolean reloadToRedis();
}
