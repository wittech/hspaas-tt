package com.huashi.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@MapperScan("com.huashi.common.**.dao")
public class HsCommonApplication {

    public static void main(String args[]) {
        SpringApplication.run(HsCommonApplication.class, args);

        LogUtils.info("华时融合公共服务项目启动!");
    }
}
