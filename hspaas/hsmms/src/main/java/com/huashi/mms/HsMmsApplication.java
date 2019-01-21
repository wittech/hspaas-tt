package com.huashi.mms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@ImportResource({ "classpath:spring-dubbo-provider.xml" })
public class HsMmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsMmsApplication.class, args);

        LogUtils.info("华时彩信服务已启动");
    }

}
