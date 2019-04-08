/**
 * 
 */
package com.huashi.mms.passage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.mms.passage.dao.MmsPassageParameterMapper;
import com.huashi.mms.passage.domain.MmsPassageParameter;

/**
 * TODO 通道参数配置服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月20日 下午11:43:46
 */
@Service
public class MmsPassageParameterService implements IMmsPassageParameterService {

    @Autowired
    private MmsPassageParameterMapper mmsPassageParameterMapper;

    @Override
    public List<MmsPassageParameter> findByPassageId(int passageId) {
        return mmsPassageParameterMapper.findByPassageId(passageId);
    }

    @Override
    public MmsPassageParameter getByType(PassageCallType callType, String passageCode) {
        return mmsPassageParameterMapper.getByTypeAndUrl(callType.getCode(), passageCode);
    }

    @Override
    public MmsPassageParameter getByPassageIdAndType(int passageId, PassageCallType callType) {
        return mmsPassageParameterMapper.selectByPassageIdAndType(passageId, callType.getCode());
    }

}
