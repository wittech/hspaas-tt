package com.huashi.web.prervice.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.third.service.ICloudFileService;

@Service
public class FileUploadPrervice {

    @Reference
    private ICloudFileService   cloudFileService;

    private final Logger        logger     = LoggerFactory.getLogger(getClass());

    private static final String FILE_USAGE = "upload";

    /**
     * TODO 文件上传
     * 
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public String upload(MultipartFile multipartFile) throws Exception {
        if (multipartFile == null) {
            throw new NullPointerException("multipartFile data is null");
        }

        logger.info("upload file args, originalFilename : {}, size : {}, contentType:{}, name:{}",
                    multipartFile.getOriginalFilename(), multipartFile.getSize(), multipartFile.getContentType(),
                    multipartFile.getName());

        return cloudFileService.writeTmpFile(multipartFile.getBytes(), FILE_USAGE);
    }

    /**
     * 
       * TODO 获取文件信息
       * @param filename
       * @return
     */
    public byte[] getFile(String filename) {
        return cloudFileService.readTmpFile(filename);
    }
}
