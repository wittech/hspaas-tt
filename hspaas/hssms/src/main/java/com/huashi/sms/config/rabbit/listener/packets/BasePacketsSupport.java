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
     * 默认每个包手机号码上限数
     */
    protected static final int DEFAULT_REQUEST_MOBILE_PACKAGE_SIZE = 4000;
    
    /**
     * 当提交数据大于等于阈值时直接调用持久化方法，无需REDIS汇聚数据
     */
    protected static final int DIRECT_PERSISTENT_SIZE_THRESHOLD = 500;

    /**
     * TODO 重组手机号码，按照分包数量进行数据拆分 分包数据
     * 
     * @param mobile 手机号码数组
     * @param mobileNumPerGroup 每组手机号码个数
     * @return
     */
    protected static List<String> regroupMobiles(String[] mobile, int mobileNumPerGroup) {
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
    
    public static void main(String[] args) {
        String[] mobile = {"1","2","3","4","5","6","7","8"};
        List<String> list = regroupMobiles(mobile, 4000);
        for(String m : list) {
            System.out.println(m);
        }
        
        System.out.println(list.size());
    }
}
