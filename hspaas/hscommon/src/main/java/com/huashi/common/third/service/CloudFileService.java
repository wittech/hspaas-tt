package com.huashi.common.third.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.util.IdGenerator;
import com.huashi.util.OssClientTemplate;

@Service
public class CloudFileService implements ICloudFileService {

    @Autowired
    private OssClientTemplate ossClientTemplate;

    @Override
    public String writeTmpFile(byte[] data, String usage) {
        String fileName = generateFileName();

        ossClientTemplate.putFile(fileName, data);

        return fileName;
    }

    private static String generateFileName() {
        return IdGenerator.generatex() + "";
    }

    @Override
    public byte[] readTmpFile(String filename) {
        return ossClientTemplate.queryFile(filename);
    }

}
