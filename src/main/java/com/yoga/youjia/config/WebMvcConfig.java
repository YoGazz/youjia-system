package com.yoga.youjia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 
 * 用于配置Spring MVC的各种组件，包括拦截器、视图解析器等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLogInterceptor requestLogInterceptor;

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/h2-console/**",      // 排除H2数据库控制台
                        "/actuator/**",        // 排除健康检测端点
                        "/swagger-ui/**",      // 排除Swagger UI
                        "/doc.html",           // 排除Knife4j文档
                        "/v3/api-docs/**",     // 排除OpenAPI文档
                        "/favicon.ico",        // 排除favicon请求
                        "/static/**",          // 排除静态资源
                        "/public/**",          // 排除公共资源
                        "/error"               // 排除错误页面
                );
    }
}