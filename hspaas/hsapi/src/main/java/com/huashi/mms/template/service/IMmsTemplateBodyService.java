package com.huashi.mms.template.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.FileResponse;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.mms.template.exception.BodyCheckException;

/**
 * 彩信模板结构体数据服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月17日 上午10:39:48
 */
public interface IMmsTemplateBodyService {

    /**
     * TODO 写入OSS文件
     * 
     * @param fileName
     * @param fileData
     * @param mediaType
     * @return
     */
    FileResponse writeOssFile(String fileName, byte[] fileData, String mediaType);

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
     * 判断BODY内容是否有效
     * 
     * @param body
     * @return
     */
    Map<String, List<MmsMessageTemplateBody>> translateBody(String body) throws BodyCheckException;

    /**
     * TODO 根据文件名称获取报文信息
     * 
     * @param resourceName
     * @return
     */
    List<MmsMessageTemplateBody> getBodies(String resourceName);
    
    /**
     * 
       * 根据模板主键获取报文信息
       * 
       * @param templateId
       * @return
     */
    List<MmsMessageTemplateBody> getBodiesByTemplateId(long templateId);

    /**
     * 默认文本文件扩展名
     */
    String defaultTextSuffixName = "txt";
}
