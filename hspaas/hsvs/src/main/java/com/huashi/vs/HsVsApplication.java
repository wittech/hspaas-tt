/**
 * 
 */
package com.huashi.vs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

/**
 * 
  * TODO 华时语音服务启动入口
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年7月3日 下午5:24:06
 */
@SpringBootApplication
@ImportResource({ "classpath:spring.xml" })
public class HsVsApplication {

	public static void main(String args[]) {
		SpringApplication.run(HsVsApplication.class, args);

		LogUtils.info("----------------华时语音服务项目已启动----------------");

	}
}
