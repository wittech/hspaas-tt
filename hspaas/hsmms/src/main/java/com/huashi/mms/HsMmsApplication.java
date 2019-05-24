package com.huashi.mms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@MapperScan("com.huashi.mms.**.dao")
public class HsMmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsMmsApplication.class, args);

        LogUtils.info("华时彩信服务已启动");
    }

}
