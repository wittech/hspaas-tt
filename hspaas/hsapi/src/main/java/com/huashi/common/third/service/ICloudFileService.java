package com.huashi.common.third.service;

public interface ICloudFileService {

    /**
     * TODO 保存临时文件
     * 
     * @param data
     * @param usage 用途
     * @return
     */
    String writeTmpFile(byte[] data, String usage);

    /**
     * TODO 获取云文件内容
     * 
     * @param filename
     * @return
     */
    byte[] readTmpFile(String filename);

}
