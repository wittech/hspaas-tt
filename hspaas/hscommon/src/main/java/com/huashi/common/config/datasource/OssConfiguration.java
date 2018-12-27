package com.huashi.common.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.huashi.util.OssClientTemplate;

@Configuration
public class OssConfiguration {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.secretAccessKey}")
    private String secretAccessKey;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Bean
    public OssClientTemplate ossClientTemplate() {
        return new OssClientTemplate(endpoint, accessKeyId, secretAccessKey, bucketName);
    }
}
