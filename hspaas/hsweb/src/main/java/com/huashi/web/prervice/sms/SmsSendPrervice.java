package com.huashi.web.prervice.sms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.huashi.web.prervice.common.FileUploadPrervice;
import com.huashi.web.util.CsvUtil;
import com.huashi.web.util.excel.ExcelUtil;

@Service
public class SmsSendPrervice {

    private static final String CONTENT_TYPE_TXT   = "txt";
    private static final String CONTENT_TYPE_EXCEL = "excel";

    /**
     * 多个手机号码分隔符号
     */
    public static final String  COMMA_SEPERATOR    = ",";

    /**
     * 内存上限大小（10M），多余10M采用云文件模式
     */
    private static final long   CACHE_LIMIT_SIZE   = 10485760l;

    private final Logger        logger             = LoggerFactory.getLogger(getClass());

    @Autowired
    private FileUploadPrervice  fileUploadPrervice;

    /**
     * TODO 在文件中读取手机号码
     * 
     * @param type
     * @param multipartFile
     * @return
     */
    public Set<String> readMobilesFromFile(String type, MultipartFile multipartFile) {
        if (StringUtils.isEmpty(type) || multipartFile == null) {
            throw new IllegalArgumentException("type[" + type + "] ， multipartFile [" + multipartFile + "]");
        }

        try {
            byte[] file = getFile(multipartFile);
            Set<String> list = null;
            if (CONTENT_TYPE_EXCEL.equalsIgnoreCase(type)) {
                list = ExcelUtil.readFirstColoum(new ByteArrayInputStream(file));
            } else if (CONTENT_TYPE_TXT.equalsIgnoreCase(type)) {
                list = CsvUtil.readFirstColomn(new ByteArrayInputStream(file));
            } else {
                throw new IllegalArgumentException("ContentType[" + type + "] not supported");
            }

            return list;

        } catch (Exception e) {
            logger.error("文件上传分析失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    /**
     * TODO 获取文件数据
     * 
     * @param multipartFile
     * @return
     * @throws IOException
     */
    private byte[] getFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.getSize() <= CACHE_LIMIT_SIZE) {
            return multipartFile.getBytes();
        }

        try {
            String filename = fileUploadPrervice.upload(multipartFile);

            return fileUploadPrervice.getFile(filename);

        } catch (Exception e) {
            logger.error("文件上传分析失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

}
