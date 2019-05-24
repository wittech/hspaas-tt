package com.huashi.web.prervice.sms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.huashi.common.notice.vo.BaseResponse;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.mms.template.constant.MmsTemplateContext.MediaType;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.mms.template.service.IMmsTemplateBodyService;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.web.client.MmsClient;

@Service
public class MmsSendPrervice {

    @Reference
    private IMmsTemplateService mmsMessageTemplateService;

    @Autowired
    private MmsClient           mmsClient;

    private final Logger        logger = LoggerFactory.getLogger(getClass());

    private boolean validate(String title, String[] mediaTypes, String[] contents, MultipartFile[] files) {
        if (StringUtils.isEmpty(title)) {
            logger.warn("标题信息为空");
            return false;
        }

        if (mediaTypes == null) {
            logger.warn("内容[mediaTypes]信息为空");
            return false;
        }

        if (contents == null) {
            logger.warn("内容[contents]信息为空");
            return false;
        }

        if (files == null) {
            logger.warn("内容[files]信息为空");
            return false;
        }

        return true;
    }

    /**
     * TODO 保存模板
     * 
     * @param userId
     * @param name
     * @param title
     * @param mediaTypes
     * @param contents
     * @param files
     * @return
     */
    public BaseResponse saveModel(int userId, String name, String title, String[] mediaTypes, String[] contents,
                                  MultipartFile[] files) {
        
        if (StringUtils.isEmpty(name)) {
            return new BaseResponse(false, "模板名称不能为空");
        }

        if (!validate(title, mediaTypes, contents, files)) {
            return new BaseResponse(false, "模板信息输入不完整");
        }

        try {
            
            return mmsClient.applyModel(userId, name, title, JSON.toJSONString(parseBody(mediaTypes, contents, files)));
        } catch (Exception e) {
            logger.error("保存彩信模板： userId:[" + userId + "]失败", e);
            return new BaseResponse(false, "保存失败");
        }
    }

    /**
     * TODO 修改模板
     * 
     * @param userId
     * @param name
     * @param title
     * @param mediaTypes
     * @param contents
     * @param files
     * @param id
     * @return
     */
    public BaseResponse updateModel(int userId, String name, String title, String[] mediaTypes, String[] contents,
                                    MultipartFile[] files, Long id) {

        if (StringUtils.isEmpty(name)) {
            return new BaseResponse(false, "模板名称不能为空");
        }
        
        if (!validate(title, mediaTypes, contents, files)) {
            return new BaseResponse(false, "模板信息输入不完整");
        }

        try {

            boolean isOk = mmsMessageTemplateService.update(parseTemplate(userId, name, title, mediaTypes, contents,
                                                                          files, id));

            return new BaseResponse(isOk);
        } catch (Exception e) {
            logger.error("修改彩信模板： userId:[" + userId + "]失败", e);
            return new BaseResponse(false, "修改失败");
        }
    }

//    private MmsMessageTemplate parseTemplate(int userId, String name, String title, String[] mediaTypes,
//                                             String[] contents, MultipartFile[] files) throws Exception {
//        return parseTemplate(userId, name, title, mediaTypes, contents, files, null);
//    }

    private MmsMessageTemplate parseTemplate(int userId, String name, String title, String[] mediaTypes,
                                             String[] contents, MultipartFile[] files, Long id) throws Exception {
        MmsMessageTemplate messageTemplate = null;

        if (id == null) {
            messageTemplate = new MmsMessageTemplate();
        } else {
            messageTemplate = mmsMessageTemplateService.get(id);
        }

        messageTemplate.setName(name);
        messageTemplate.setTitle(title);
        messageTemplate.setUserId(userId);
        messageTemplate.setAppType(AppType.WEB.getCode());

        messageTemplate.setBodies(parseBody(mediaTypes, contents, files));

        return messageTemplate;
    }

    /**
     * TODO 转换成BODY
     * 
     * @param mediaTypes
     * @param contents
     * @param files
     * @param fileSuffixName
     * @return
     * @throws Exception
     */
    private List<MmsMessageTemplateBody> parseBody(String[] mediaTypes, String[] contents, MultipartFile[] files)
                                                                                                                 throws Exception {
        if (mediaTypes == null || mediaTypes.length == 0) {
            throw new IllegalArgumentException("无彩信内容");
        }

        List<MmsMessageTemplateBody> bodies = new ArrayList<>();
        int textHappensCount = 0;
        int fileHappensCount = 0;

        try {
            for (String mediaType : mediaTypes) {
                MmsMessageTemplateBody body = new MmsMessageTemplateBody();
                body.setMediaType(mediaType);
                if (MediaType.TEXT.getCode().equalsIgnoreCase(mediaType)) {
                    body.setMediaName(IMmsTemplateBodyService.defaultTextSuffixName);
                    body.setContent(contents[textHappensCount]);
                    textHappensCount++;
                } else {
                    body.setMediaName(getExtensionName(files[fileHappensCount]));
                    body.setContent(file2Base64(files[fileHappensCount]));

                    fileHappensCount++;
                }

                bodies.add(body);
            }

            return bodies;
        } catch (Exception e) {
            throw e;
        }
    }

    private String getExtensionName(MultipartFile file) throws IOException {
        logger.info("contentType:" + file.getContentType() + " name:" + file.getName() + " orgiginalName:"
                    + file.getOriginalFilename());

        if ((file.getOriginalFilename() != null) && (file.getOriginalFilename().length() > 0)) {
            int dot = file.getOriginalFilename().lastIndexOf('.');
            if ((dot > -1) && (dot < (file.getOriginalFilename().length() - 1))) {
                return file.getOriginalFilename().substring(dot + 1);
            }
        }
        return file.getOriginalFilename();
    }

    private String file2Base64(MultipartFile file) throws IOException {
        logger.info("contentType:" + file.getContentType() + " name:" + file.getName() + " orgiginalName:"
                    + file.getOriginalFilename());
        return Base64.encodeBase64String(file.getBytes());
    }

    /**
     * TODO 发送彩信
     * 
     * @param userId
     * @param mobile
     * @param name
     * @param title
     * @param mediaTypes
     * @param contents
     * @param files
     * @return
     */
    public BaseResponse sendMms(int userId, String mobile, String title, String[] mediaTypes,
                                String[] contents, MultipartFile[] files) {

        if (!validate(title, mediaTypes, contents, files)) {
            return new BaseResponse(false, "彩信信息输入不完整");
        }

        try {

            return mmsClient.sendMms(userId, mobile, title, JSON.toJSONString(parseBody(mediaTypes, contents, files)));

        } catch (Exception e) {
            logger.error("发送彩信模板： userId:[" + userId + "]失败", e);
            return new BaseResponse(false, "发送失败");
        }
    }

    /**
     * TODO 模板发送
     * 
     * @param userId
     * @param mobile
     * @param modelId
     * @return
     */
    public BaseResponse sendMmsByModel(int userId, String mobile, String modelId) {
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(modelId)) {
            return new BaseResponse(false, "彩信信息输入不完整");
        }

        try {

            return mmsClient.sendMmsByModel(userId, mobile, modelId);

        } catch (Exception e) {
            logger.error("发送彩信模板： userId:[" + userId + "]失败", e);
            return new BaseResponse(false, "发送失败");
        }
    }

}
