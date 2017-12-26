package com.huashi.sms.config.rabbit.listener.packets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePacketsSupport {

    protected Logger              logger                  = LoggerFactory.getLogger(getClass());

    // 错误信息分隔符
    protected static final String ERROR_MESSAGE_SEPERATOR = ";";

    /**
     * TODO 重组手机号码，按照分包数量进行数据拆分 分包数据
     * 
     * @param mobile 手机号码数组
     * @param mobileNumPerGroup 每组手机号码个数
     * @return
     */
    static List<String> regroupMobiles(String[] mobile, int mobileNumPerGroup) {
        int totalSize = mobile.length;
        // 获取要拆分子数组个数
        int count = (totalSize % mobileNumPerGroup == 0) ? (totalSize / mobileNumPerGroup) : (totalSize
                                                                                              / mobileNumPerGroup + 1);

        List<String> rows = new ArrayList<>();
        StringBuilder builder = null;
        for (int i = 0; i < count; i++) {

            int index = i * mobileNumPerGroup;
            builder = new StringBuilder();

            for (int j = 0; j < mobileNumPerGroup && index < totalSize; j++) {
                builder.append(mobile[index++]).append(",");
            }

            rows.add(builder.substring(0, builder.length() - 1));
        }
        return rows;
    }
}
