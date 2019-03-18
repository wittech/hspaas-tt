package com.huashi.mms.template.service;

import java.util.List;

import com.huashi.mms.template.domain.MmsMessageTemplateBody;

/**
 * 彩信模板结构体数据服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月17日 上午10:39:48
 */
public interface IMmsTemplateBodyService {

    /**
     * TODO 根据模板ID查询全部报文体数据
     * 
     * @param templateId
     * @return
     */
    List<MmsMessageTemplateBody> find(Long templateId);

    /**
     * TODO 批量保存
     * 
     * @param bodies
     * @return
     */
    boolean batchSave(List<MmsMessageTemplateBody> bodies);

    /**
     * TODO 根据模板ID删除
     * 
     * @param templateId
     * @return
     */
    boolean delete(Long templateId);

    /**
     * TODO 根据文件名称获取报文信息
     * 
     * @param resourceName
     * @return
     */
    List<MmsMessageTemplateBody> getBodies(String resourceName);
}
