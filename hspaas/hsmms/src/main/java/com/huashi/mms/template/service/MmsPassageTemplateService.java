package com.huashi.mms.template.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.mms.template.dao.MmsPassageMessageTemplateMapper;
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
    private MmsPassageMessageTemplateMapper mmsPassageMessageTemplateMapper;

    private final Logger                    logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean save(MmsPassageMessageTemplate template) {
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());
        return mmsPassageMessageTemplateMapper.insert(template) > 0;
    }

    @Override
    public boolean updateStatus(long id, int status, String remark) {
        MmsPassageMessageTemplate template = get(id);
        if (template == null) {
            logger.error("模板ID[" + id + "]数据为空，更新失败");
            return false;
        }

        template.setStatus(status);
        template.setRemark(remark);

        return mmsPassageMessageTemplateMapper.updateByPrimaryKey(template) > 0;
    }

    @Override
    public boolean update(MmsPassageMessageTemplate template) {
        template.setUpdateTime(new Date());
        return mmsPassageMessageTemplateMapper.updateByPrimaryKey(template) > 0;
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
        return mmsPassageMessageTemplateMapper.selectByMmsTemplateId(templateId);
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
