package com.huashi.mms.template.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.vo.FileResponse;
import com.huashi.mms.template.constant.MediaFileDirectory;
import com.huashi.mms.template.constant.MmsTemplateContext.MediaType;
import com.huashi.mms.template.dao.MmsMessageTemplateBodyMapper;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.mms.template.exception.BodyCheckException;

@Service
public class MmsTemplateBodyService implements IMmsTemplateBodyService {

    @Autowired
    private MmsMediaFileService          mmsMediaFileService;
    @Autowired
    private MmsMessageTemplateBodyMapper mmsMessageTemplateBodyMapper;
    @Autowired
    private IMmsTemplateService          mmsTemplateService;

    /**
     * 多媒体类型
     */
    private static final String          NODE_MEDIA_TYPE          = "mediaType";

    /**
     * 多媒体文件名称
     */
    private static final String          NODE_MEDIA_NAME          = "mediaName";

    /**
     * 多媒体内容
     */
    private static final String          NODE_CONTENT             = "content";

    /**
     * 报文文件扩展名
     */
    private static final String          BODY_FILE_EXTENSION_NAME = "json";

    private final Logger                 logger                   = LoggerFactory.getLogger(getClass());

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

    private void fillResource(List<MmsMessageTemplateBody> bodies) throws UnsupportedEncodingException {
        for (MmsMessageTemplateBody body : bodies) {
            if (StringUtils.isBlank(body.getContent())) {
                continue;
            }

            body.setData(mmsMediaFileService.readFile(body.getContent()));

            if (!MediaType.TEXT.getCode().equals(body.getMediaType())) {
                // 转义多媒体网络URL
                body.setContent(mmsMediaFileService.getWebUrl(body.getContent()));
            } else {
                body.setContent(new String(mmsMediaFileService.readFile(body.getContent()),
                                           MmsMediaFileService.ENCODING));
            }
        }
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

            List<MmsMessageTemplateBody> bodies = JSON.parseObject(context,
                                                                   new TypeReference<List<MmsMessageTemplateBody>>() {
                                                                   });

            fillResource(bodies);

            return bodies;
        } catch (Exception e) {
            logger.error("ResourceName[" + resourceName + "]'s data translate to string failed", e);
        }

        return null;
    }

    @Override
    public boolean delete(Long templateId) {
        return mmsMessageTemplateBodyMapper.deleteByTemplateId(templateId) > 0;
    }

    @Override
    public FileResponse writeOssFile(String extensionName, byte[] fileData, String mediaType) {
        try {
            String filename = mmsMediaFileService.writeFile(mediaType, fileData, extensionName);
            if (StringUtils.isEmpty(filename)) {
                return new FileResponse(false, "File gerenate failed");
            }

            return new FileResponse(true, "success", filename);

        } catch (Exception e) {
            logger.error("上传writeOssFile失败", e);
            return new FileResponse(false, "File gerenate failed");
        }
    }

    @Override
    public Map<String, List<MmsMessageTemplateBody>> translateBody(String body) throws BodyCheckException {
        // body:[{mediaName:”test.jpg”,mediaType:”image”,content:”=bS39888993#jajierj*...”}]
        if (StringUtils.isEmpty(body)) {
            throw new BodyCheckException("Body is empty.");
        }

        try {
            List<Map<String, String>> list = JSON.parseObject(body, new TypeReference<List<Map<String, String>>>() {
            });

            List<MmsMessageTemplateBody> bodyReport = new ArrayList<>();
            int sort = 0;
            for (Map<String, String> map : list) {
                String mediaType = map.get(NODE_MEDIA_TYPE);
                if (StringUtils.isEmpty(mediaType)) {
                    throw new BodyCheckException("Body's node[mediaType] is empty");
                }

                if (!MediaType.isTypeRight(mediaType)) {
                    throw new BodyCheckException("Body's node[mediaType:'" + mediaType + "'] is not avaiable.");
                }

                String mediaName = map.get(NODE_MEDIA_NAME);
                if (StringUtils.isEmpty(mediaName)) {
                    throw new BodyCheckException("Body's node[mediaName] is empty.");
                }

                String content = map.get(NODE_CONTENT);
                if (StringUtils.isEmpty(content)) {
                    throw new BodyCheckException("Body's node[content] is empty.");
                }

                boolean isUtf8 = !isByteType(mediaType);

                byte[] data = parse2Byte(content, isUtf8);
                if (data == null) {
                    throw new BodyCheckException("Body's node[content:'" + content + "'] translate failed.");
                }

                sort++;
                MmsMessageTemplateBody templateBody = new MmsMessageTemplateBody();
                templateBody.setMediaName(mediaName.toLowerCase());
                templateBody.setMediaType(mediaType.toLowerCase());
                templateBody.setData(data);
                templateBody.setContent(mmsMediaFileService.generateFileName(MediaFileDirectory.CUSTOM_BODY_FILE_DIR,
                                                                             templateBody.getMediaType(),
                                                                             templateBody.getMediaName()));
                templateBody.setSort(sort);

                bodyReport.add(templateBody);
            }

            if (CollectionUtils.isNotEmpty(bodyReport)) {
                for (MmsMessageTemplateBody templateBody : bodyReport) {
                    mmsMediaFileService.writeFileWithFileName(templateBody.getContent(), templateBody.getData());
                }

                Map<String, List<MmsMessageTemplateBody>> result = new HashMap<>();
                result.put(mmsMediaFileService.writeFile(MediaFileDirectory.CUSTOM_BODY_DIR,
                                                         JSON.toJSONString(bodyReport), BODY_FILE_EXTENSION_NAME),
                           bodyReport);

                return result;
            }

            throw new BodyCheckException("Unknown exception");
        } catch (Exception e) {
            logger.error("自定义内容[" + body + "]解析错误", e);
            throw new BodyCheckException("Body translate failed");
        }
    }

    /**
     * 是否属于BYTE模式
     * 
     * @param mediaType
     * @return
     */
    private static boolean isByteType(String mediaType) {
        if (StringUtils.isEmpty(mediaType)) {
            return false;
        }

        return MediaType.IMAGE.getCode().equals(mediaType) || MediaType.AUDIO.getCode().equals(mediaType)
               || MediaType.VIDEO.getCode().equals(mediaType);
    }

    /**
     * 转换成为BYTE数据
     * 
     * @param content
     * @return
     */
    private byte[] parse2Byte(String content, boolean isUtf8) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        try {
            if (isUtf8) {
                return content.getBytes(Charset.forName(MmsMediaFileService.ENCODING));
            }

            return Base64.decodeBase64(content);
        } catch (Exception e) {
            logger.error("内容[" + content + "]转换Base64失败", e);
            return null;
        }
    }

    @Override
    public List<MmsMessageTemplateBody> getBodiesByTemplateId(long templateId) {
        try {
            List<MmsMessageTemplateBody> bodies = find(templateId);
            if (CollectionUtils.isEmpty(bodies)) {
                return null;
            }

            fillResource(bodies);

            return bodies;
        } catch (Exception e) {
            logger.error("getBodiesByTemplateId [" + templateId + "] failed", e);
            return null;
        }
    }

    @Override
    public List<MmsMessageTemplateBody> getBodiesByModelId(String modelId) {
        try {

            MmsMessageTemplate template = mmsTemplateService.getByModelId(modelId);
            if (template == null) {
                logger.error("getBodiesByModelId [" + modelId + "] find template is null");
                return null;
            }

            List<MmsMessageTemplateBody> bodies = find(template.getId());
            if (CollectionUtils.isEmpty(bodies)) {
                return null;
            }

            fillResource(bodies);

            return bodies;
        } catch (Exception e) {
            logger.error("getBodiesByModelId [" + modelId + "] failed", e);
            return null;
        }
    }

}
