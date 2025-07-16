package com.yoga.youjia.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * 用于配置Knife4j的相关设置
 * @Configuration 注解表示这是一个配置类
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("佑珈测试项目")
                        .version("1.0.0")
                        .description("这是一个测试项目."));
    }
}
