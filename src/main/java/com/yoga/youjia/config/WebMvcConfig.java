package com.yoga.youjia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * 用于配置Spring MVC的各种组件，包括拦截器、CORS跨域配置等
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

    /**
     * 配置CORS跨域
     *
     * 明确指定允许的前端地址，提高安全性
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 对所有API接口启用CORS
                .allowedOrigins(
                        "http://localhost:3000",   // React开发服务器默认端口
                        "http://localhost:3001"    // 备用前端端口
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")                    // 允许所有请求头
                .allowCredentials(true)                 // 允许发送Cookie和认证信息
                .maxAge(3600);                         // 预检请求缓存时间（秒）
    }
}
