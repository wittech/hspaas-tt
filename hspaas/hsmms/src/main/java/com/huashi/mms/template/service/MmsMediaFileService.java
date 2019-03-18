package com.huashi.mms.template.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huashi.common.util.IdGenerator;
import com.huashi.mms.config.datasource.AliyunOssStorage;
import com.huashi.mms.template.constant.MediaFileDirectory;

@Service
public class MmsMediaFileService {

    /**
     * 默认编码
     */
    public static final String ENCODING = "UTF-8";

    @Autowired
    private AliyunOssStorage   aliyunOssStorage;

    public String writeFile(String dir, String data) {
        try {
            return writeFile(dir, data.getBytes(ENCODING));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * TODO 写入OSS文件
     * 
     * @param originName 原文件名称
     * @param filename 新文件名称
     * @param data 二进制数据
     * @return
     */
    public String writeFileWithFileName(String filename, byte[] data) {
        boolean isOk = aliyunOssStorage.putFile(filename, data);
        if (isOk) {
            return filename;
        }

        throw new RuntimeException("Writing file failed");
    }

    /**
     * TODO 写入OSS文件
     * 
     * @param originName
     * @param dir
     * @param data
     * @return
     */
    public String writeFile(String dir, byte[] data) {
        return writeFileWithFileName(generateFileName(dir), data);
    }

    public String generateFileName() {
        return generateFileName(null, null);
    }

    public String generateFileName(String dir) {
        return generateFileName(dir, null);
    }

    /**
     * TODO 生成文件名称
     * 
     * @param dir 一级路径
     * @param secondDir 二级路径
     * @return
     */
    public String generateFileName(String dir, String secondDir) {
        StringBuilder filename = new StringBuilder();
        if (StringUtils.isNotBlank(dir)) {
            filename.append(MediaFileDirectory.DIR_SEPERATOR).append(dir);
        }

        if (StringUtils.isNotBlank(secondDir)) {
            filename.append(MediaFileDirectory.DIR_SEPERATOR).append(secondDir);
        }

        filename.append(MediaFileDirectory.DIR_SEPERATOR).append(IdGenerator.generatex());

        return filename.toString();
    }

    public byte[] readFile(String filename) {
        return aliyunOssStorage.queryFile(filename);
    }

}
