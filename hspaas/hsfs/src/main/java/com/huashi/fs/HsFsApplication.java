/**
 * 
 */
package com.huashi.fs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

/**
 * 
  * TODO 华时流量服务启动入口
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年7月20日 下午3:01:25
 */
@SpringBootApplication
@ImportResource({ "classpath:spring.xml" })
public class HsFsApplication {

	public static void main(String args[]){

		SpringApplication.run(HsFsApplication.class, args);

		LogUtils.info("----------------华时流量服务项目已启动----------------");

	}
}
