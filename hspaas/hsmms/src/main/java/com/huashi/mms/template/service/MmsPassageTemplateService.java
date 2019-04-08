package com.huashi.mms.template.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.passage.service.IMmsPassageParameterService;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.template.constant.MmsTemplateContext.PassageTemplateStatus;
import com.huashi.mms.template.dao.MmsPassageMessageTemplateMapper;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsPassageMessageTemplate;

/**
 * TODO 通道方彩信模板服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月17日 上午12:59:52
 */
@Service
public class MmsPassageTemplateService implements IMmsPassageTemplateService {

    @Autowired
    private IMmsPassageService              mmsPassageService;
    @Reference
    private IMmsProviderService             mmsProviderService;
    @Autowired
    private IMmsTemplateService             mmsTemplateService;
    @Autowired
    private IMmsTemplateBodyService         mmsTemplateBodyService;
    @Autowired
    private IMmsPassageParameterService     mmsPassageParameterService;
    @Autowired
    private MmsPassageMessageTemplateMapper mmsPassageMessageTemplateMapper;

    private final Logger                    logger = LoggerFactory.getLogger(getClass());

    /**
     * 报备模板至通道接口
     * 
     * @param passageId
     * @param templateId
     * @return
     */
    private String sendModel2Passage(int passageId, long templateId) {
        try {
            MmsPassageParameter parameter = mmsPassageParameterService.getByPassageIdAndType(passageId,
                                                                                             PassageCallType.MODEL_REPORT_SUBMIT);
            if (parameter == null) {
                logger.error("Applying model[passageId" + parameter + "] exception cause by 'parameter is null'");
                return null;
            }

            MmsMessageTemplate mmsMessageTemplate = mmsTemplateService.get(templateId);
            if (mmsMessageTemplate == null) {
                logger.error("Applying model[template:" + templateId + "] exception cause by 'messageTemplate is null'");
                return null;
            }
            
            mmsMessageTemplate.setBodies(mmsTemplateBodyService.getBodiesByTemplateId(templateId));

            // 调用模板报备
            ProviderModelResponse response = mmsProviderService.applyModel(parameter, mmsMessageTemplate);
            if (!response.isSucceess()) {
                logger.error("Applying model failed cause by 'Resonpse is [" + JSON.toJSONString(response) + "]'");
                return null;
            }

            return response.getModelId();
        } catch (Exception e) {
            logger.warn("Applying model exception : {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean save(MmsPassageMessageTemplate mmsPassageMessageTemplate) {

        try {
            MmsPassageMessageTemplate old = getByMmsTemplateIdAndPassageId(mmsPassageMessageTemplate.getTemplateId(),
                                                                           mmsPassageMessageTemplate.getPassageId());
            if (old != null) {
                logger.error("改通道模板[" + JSON.toJSONString(mmsPassageMessageTemplate) + "]已存在");
                return false;
            }

            mmsPassageMessageTemplate.setPassageModelId(sendModel2Passage(mmsPassageMessageTemplate.getPassageId(),
                                                                          mmsPassageMessageTemplate.getTemplateId()));

            mmsPassageMessageTemplate.setStatus(PassageTemplateStatus.WAITING.getValue());
            mmsPassageMessageTemplate.setCreateTime(new Date());
            mmsPassageMessageTemplate.setUpdateTime(new Date());
            return mmsPassageMessageTemplateMapper.insert(mmsPassageMessageTemplate) > 0;
        } catch (Exception e) {
            logger.error("MmsPassageMessageTemplate[" + JSON.toJSONString(mmsPassageMessageTemplate) + "] save failed",
                         e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }

    @Override
    public boolean update(MmsPassageMessageTemplate template) {
        MmsPassageMessageTemplate originTemplate = get(template.getId());
        if (originTemplate == null) {
            logger.error("模板ID[" + template.getId() + "]数据为空，更新失败");
            return false;
        }

        originTemplate.setPassageModelId(template.getPassageModelId());
        originTemplate.setStatus(template.getStatus());
        originTemplate.setRemark(template.getRemark());
        originTemplate.setUpdateTime(new Date());

        return mmsPassageMessageTemplateMapper.updateByPrimaryKey(originTemplate) > 0;
    }

    @Override
    public boolean deleteById(long id) {
        return mmsPassageMessageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public MmsPassageMessageTemplate get(long id) {
        return mmsPassageMessageTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<MmsPassageMessageTemplate> getByMmsTemplateId(long templateId) {
        List<MmsPassageMessageTemplate> list = mmsPassageMessageTemplateMapper.selectByMmsTemplateId(templateId);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }

        for (MmsPassageMessageTemplate t : list) {
            MmsPassage passage = mmsPassageService.findById(t.getPassageId());
            t.setPassageName(passage == null || StringUtils.isEmpty(passage.getName()) ? "未知" : passage.getName());
            PassageTemplateStatus status = PassageTemplateStatus.parse(t.getStatus());
            t.setStatusText(status == null ? "未定义" : status.getTitle());
        }

        return list;
    }

    @Override
    public boolean reloadToRedis() {
        return false;
    }

    @Override
    public MmsPassageMessageTemplate getByMmsTemplateIdAndPassageId(long templateId, int passageId) {
        return mmsPassageMessageTemplateMapper.selectByMmsTemplateIdAndPassageId(templateId, passageId);
    }

    @Override
    public MmsPassageMessageTemplate getByMmsModelIdAndPassageId(String modelId, int passageId) {
        return mmsPassageMessageTemplateMapper.selectByMmsModelIdAndPassageId(modelId, passageId);
    }

}
