/**
 * 
 */
package com.huashi.mms.passage.service;

import java.util.List;

import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.mms.passage.domain.MmsPassageParameter;

/**
 * TODO 通道参数服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月14日 下午4:00:22
 */
public interface IMmsPassageParameterService {

    /**
     * 根据通道ID和调用类型查询通道参数信息
     * 
     * @param passageId
     * @param callType
     * @return
     */
    MmsPassageParameter getByPassageIdAndType(int passageId, PassageCallType callType);

    /**
     * 根据通道id获取通道参数
     * 
     * @param passageId
     * @return
     */
    List<MmsPassageParameter> findByPassageId(int passageId);

    /**
     * 根据通道代码获取参数详细信息（主要针对回执报告和上行信息）
     * 
     * @param passageCode 通道代码（当通道调用类型为 状态回执推送 或 上行推送时，passage_url 值为 通道代码[唯一]）
     * @param callType 通道调用类型，本例主要用于[状态回执推送,上行推送]
     * @return
     */
    MmsPassageParameter getByType(PassageCallType callType, String passageCode);
}
