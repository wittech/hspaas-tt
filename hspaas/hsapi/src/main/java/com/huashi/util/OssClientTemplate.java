package com.huashi.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.OSSObject;

/**
 * TODO 阿里云OSS客户端工具类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年12月22日 下午9:09:16
 */
public class OssClientTemplate {

    /**
     * 地域节点
     */
    private String                    endpoint;

    /**
     * 鉴权用户ID
     */
    private String                    accessKeyId;

    /**
     * 鉴权密钥
     */
    private String                    secretAccessKey;

    /**
     * 目录名称
     */
    private String                    bucketName;

    /**
     * OSS配置信息
     */
    private ClientConfiguration       clientConfiguration;

    private static volatile OSSClient ossClient;
    private final Object              monitor = new Object();

    private final Logger              logger  = LoggerFactory.getLogger(OssClientTemplate.class);

    public OssClientTemplate(String endpoint, String accessKeyId, String secretAccessKey, String bucketName,
                             ClientConfiguration clientConfiguration) {
        super();
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.bucketName = bucketName;
        this.clientConfiguration = clientConfiguration;
    }

    public OssClientTemplate(String endpoint, String accessKeyId, String secretAccessKey, String bucketName) {
        this(endpoint, accessKeyId, secretAccessKey, bucketName, null);
    }

    /**
     * TODO 获取OSS客户端实例
     * 
     * @return
     */
    @PostConstruct
    public void initial() {
        if (ossClient == null) {

            synchronized (monitor) {
                ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey),
                                          clientConfiguration);
            }
        }
    }

    private OSSClient getOssClient() {
        if (ossClient == null) {
            initial();
        }

        if (ossClient == null) {
            throw new NullPointerException("Oss initial failed");
        }

        return ossClient;
    }

    /**
     * TODO 生成文件
     * 
     * @param name 文件主键KEY
     * @param data 文件数据
     * @return
     */
    public void putFile(String name, byte[] data) {
        getOssClient().putObject(bucketName, name, new ByteArrayInputStream(data));
    }

    /**
     * TODO 删除文件
     * 
     * @param name
     * @return
     */
    public void deleteFile(String name) {
        getOssClient().deleteObject(bucketName, name);
    }

    /**
     * TODO 查询文件
     * 
     * @param name
     * @return
     * @throws Throwable
     */
    public byte[] queryFile(String name) {
        OSSObject ossObject = getOssClient().getObject(bucketName, name);
        try (InputStream stream = ossObject.getObjectContent()) {
            return IOUtils.readStreamAsByteArray(stream);
        } catch (Throwable e) {
            logger.error("Excuting queryFile[" + name + "] failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 拷贝文件
     * 
     * @param sourceKey 原文件名称
     * @param targetKey 新文件名称
     */
    public void copyFile(String sourceKey, String targetKey) {
        getOssClient().copyObject(bucketName, sourceKey, bucketName, targetKey);
    }

    @PreDestroy
    public void shutdown() {
        getOssClient().shutdown();
    }

    /**
     * TODO 文件是否存在
     * 
     * @param name
     * @return
     */
    public boolean isExists(String name) {
        return getOssClient().doesObjectExist(bucketName, name);
    }

}
