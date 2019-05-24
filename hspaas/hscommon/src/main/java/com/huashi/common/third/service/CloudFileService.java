package com.huashi.common.third.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.config.datasource.AliyunOssStorage;
import com.huashi.common.util.IdGenerator;

@Service
public class CloudFileService implements ICloudFileService {

    @Autowired
    private AliyunOssStorage aliyunOssStorage;

    @Override
    public String writeTmpFile(byte[] data, String usage) {
        String fileName = generateFileName();

        aliyunOssStorage.putFile(fileName, data);

        return fileName;
    }

    private static String generateFileName() {
        return IdGenerator.generatex() + "";
    }

    @Override
    public byte[] readTmpFile(String filename) {
        return aliyunOssStorage.queryFile(filename);
    }

}
