package com.huashi.sms.signature.service;

import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.signature.domain.SignatureExtNo;

/**
 * 
  * TODO 签名扩展号码服务（一个用户多个签名对应不同扩展号码）
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年7月9日 下午9:19:18
 */
public interface ISignatureExtNoService {

	/**
	 * 
	   * TODO 查询签名扩展号信息
	   * 
	   * @param condition
	   * @return
	 */
	BossPaginationVo<SignatureExtNo> findPage(Map<String, Object> condition);

	/**
	 * 
	   * TODO 保存
	   * 
	   * @param signatureExtNo
	   * @return
	 */
	boolean save(SignatureExtNo signatureExtNo);

	/**
	 * 
	   * TODO 修改
	   * 
	   * @param signatureExtNo
	   * @return
	 */
	boolean update(SignatureExtNo signatureExtNo);

	/**
	 * 
	 * TODO 删除模板
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	   * TODO 根据ID查询签名扩展号信息
	   * 
	   * @param id
	   * @return
	 */
	SignatureExtNo get(Long id);

	/**
	 * 
	 * TODO 将签名扩展号数据加载到REDIS
	 * 
	 * @return
	 */
	boolean reloadToRedis();

	/**
	 * 
	   * TODO 根据内容模糊匹配得到扩展号信息（起始内容或者结尾内容包括 匹配签名）
	   * 
	   * @param userId
	   * @param content
	   * @return
	 */
	String getExtNumber(Integer userId, String content);
	
}
