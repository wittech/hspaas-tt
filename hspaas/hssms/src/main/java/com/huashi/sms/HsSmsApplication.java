package com.huashi.sms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@MapperScan("com.huashi.sms.**.dao")
public class HsSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsSmsApplication.class, args);
        LogUtils.info("华时短信服务项目已启动");
    }

}
