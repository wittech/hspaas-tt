package com.huashi.mms.template.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.mms.template.dao.MmsMessageTemplateBodyMapper;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;

@Service
public class MmsTemplateBodyService implements IMmsTemplateBodyService {

    @Autowired
    private MmsMediaFileService          mmsMediaFileService;

    @Autowired
    private MmsMessageTemplateBodyMapper mmsMessageTemplateBodyMapper;

    private final Logger                 logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<MmsMessageTemplateBody> find(Long templateId) {
        return mmsMessageTemplateBodyMapper.selectByTemplateId(templateId);
    }

    @Override
    public boolean batchSave(List<MmsMessageTemplateBody> bodies) {
        if (CollectionUtils.isEmpty(bodies)) {
            return false;
        }

        return mmsMessageTemplateBodyMapper.batchInsert(bodies) > 0;
    }

    @Override
    public List<MmsMessageTemplateBody> getBodies(String resourceName) {
        if (StringUtils.isBlank(resourceName)) {
            logger.error("ResourceName is empty");
            return null;
        }

        byte[] contextBuffer = mmsMediaFileService.readFile(resourceName);
        if (contextBuffer == null) {
            logger.error("ResourceName[" + resourceName + "]'s data is null");
            return null;
        }

        try {
            String context = new String(contextBuffer, MmsMediaFileService.ENCODING);
            if (StringUtils.isEmpty(context)) {
                logger.error("ResourceName[" + resourceName + "]'s string data is empty");
                return null;
            }

            return JSON.parseObject(context, new TypeReference<List<MmsMessageTemplateBody>>() {
            });
        } catch (Exception e) {
            logger.error("ResourceName[" + resourceName + "]'s data translate to string failed", e);
        }

        return null;
    }

    @Override
    public boolean delete(Long templateId) {
        return mmsMessageTemplateBodyMapper.deleteByTemplateId(templateId) > 0;
    }

}
