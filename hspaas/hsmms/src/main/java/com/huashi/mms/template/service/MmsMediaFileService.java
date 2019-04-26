package com.huashi.mms.template.service;

import org.apache.commons.codec.binary.Base64;
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
    public static final String  ENCODING                      = "UTF-8";

    /**
     * 文件扩展名分隔符
     */
    private static final String FILE_EXTENSION_NAME_CHARACTOR = ".";

    /**
     * 文件名参数分隔符
     */
    private static final String FILE_NAME_PARAMS_CHARACTOR    = "?";

    @Autowired
    private AliyunOssStorage    aliyunOssStorage;

    /**
     * 写入OSS
     * 
     * @param dir 文件路径
     * @param data 文件数据
     * @param extensionName 扩展名
     * @return
     */
    public String writeFile(String dir, String data, String extensionName) {
        try {
            return writeFile(dir, data.getBytes(ENCODING), extensionName);
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
     * @param dir 路径
     * @param data 文件数据
     * @param extensionName 扩展名
     * @return
     */
    public String writeFile(String dir, byte[] data, String extensionName) {
        return writeFileWithFileName(generateFileName(dir, extensionName), data);
    }

    private String generateFileName(String dir, String extensionName) {
        return generateFileName(dir, null, extensionName);
    }

    /**
     * TODO 生成文件名称
     * 
     * @param dir 一级路径
     * @param secondDir 二级路径
     * @return
     */
    public String generateFileName(String dir, String secondDir, String extensionName) {
        StringBuilder filename = new StringBuilder();
        if (StringUtils.isNotBlank(dir)) {
            filename.append(dir).append(MediaFileDirectory.DIR_SEPERATOR);
        }

        if (StringUtils.isNotBlank(secondDir)) {
            filename.append(secondDir).append(MediaFileDirectory.DIR_SEPERATOR);
        }

        filename.append(IdGenerator.generatex());

        if (StringUtils.isNotBlank(extensionName)) {
            filename.append(FILE_EXTENSION_NAME_CHARACTOR).append(extensionName);
        }

        return filename.toString();
    }

    public byte[] readFile(String filename) {
        return aliyunOssStorage.queryFile(filename);
    }

    public String readFileBase64(String filename) {
        byte[] file = readFile(filename);
        if (file == null) {
            return "";
        }

        return Base64.encodeBase64String(file);
    }

    /**
     * 获取最终的URL
     * 
     * @param path
     * @return
     */
    public String getWebUrl(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        // 需要截取URL后面的授权参数，防止浏览器不识别（有些上家通道需要强校验扩展名）
        String url = aliyunOssStorage.getUrl(name);
        if (url.contains(FILE_NAME_PARAMS_CHARACTOR)) {
            url = url.substring(0, url.indexOf(FILE_NAME_PARAMS_CHARACTOR));
        }

        return url;
    }

}
