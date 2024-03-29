package com.huashi.developer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.huashi.developer.api"))
                .paths(PathSelectors.any())
                .build();
    }
	
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("华时融合平台RESTful APIS 在线手册")
                .description("更多华时融合平台接口请查看：http://docs.hspaas.cn/swagger.html")
                .termsOfServiceUrl("http://blog.hspaas.cn/")
                .contact(new Contact("tenx", "http://api.hspaas.cn/", "service@hspaas.cn"))
                .version("1.0.1")
                .build();
    }	
}
