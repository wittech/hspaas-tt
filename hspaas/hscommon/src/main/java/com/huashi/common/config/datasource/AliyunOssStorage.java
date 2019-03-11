package com.huashi.common.config.datasource;

import javax.annotation.PreDestroy;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 私钥阿里云OSS服务
 *
 * @author zhengying
 * @version V1.0
 * @date 2018年12月14日 下午5:31:44
 */
@Service
public class AliyunOssStorage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OSSClient    ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String       aliyunOssBucketName;

    @PreDestroy
    public void shutdown() {
        ossClient.shutdown();
    }

    /**
     * 生成文件
     */
    public boolean putFile(String name, byte[] data) {
        try {
            PutObjectResult putObjectResult = ossClient.putObject(aliyunOssBucketName, name,
                                                                  new ByteArrayInputStream(data));
            if (putObjectResult == null) {
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error("Executing putFile[" + name + ", " + data.length + "] failed", e);
            return false;
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String name) {
        try {
            ossClient.deleteObject(aliyunOssBucketName, name);
        } catch (OSSException | ClientException e) {
            logger.error("Executing deleteFile[" + name + "] failed", e);
            return false;
        }

        return true;
    }

    /**
     * 查询文件
     */
    public byte[] queryFile(String name) {
        OSSObject ossObject;
        try {
            ossObject = ossClient.getObject(aliyunOssBucketName, name);

            InputStream stream = ossObject.getObjectContent();
            return IOUtils.readStreamAsByteArray(stream);
        } catch (Exception e) {
            logger.error("Executing queryFile[" + name + "] failed", e);
            return null;
        }
    }

}
