/**
 * 
 */
package com.huashi.common.provider.service;

import java.util.List;

import com.huashi.common.provider.domain.ProviderApiCode;

/**
 * 状态码服务接口类
 * @author Administrator
 *
 */
public interface IProviderApiCodeService {
    /**
     * 根据类型和状态码查询记录
     * @param type 1：短信，2:流量，3：语音
     * @param code 状态码
     * @return
     */
    List<ProviderApiCode> findTypeAndCodeRecord(int type,String code);
}
