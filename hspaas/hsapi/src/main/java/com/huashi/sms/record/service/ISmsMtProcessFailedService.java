/**
 * 
 */
package com.huashi.sms.record.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.record.domain.SmsMtProcessFailed;

/**
 * 
 * TODO 处理失败短信
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月28日 下午11:41:40
 */
public interface ISmsMtProcessFailedService {

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param smsMtManualHandling
	 * @return
	 */
	boolean save(SmsMtProcessFailed smsMtProcessFailed);

	BossPaginationVo<SmsMtProcessFailed> findPage(int pageNum,String keyword,Long sid);
}
