package com.yoga.youjia.config;

import com.yoga.youjia.security.filter.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置类
 *
 * 这个类用于配置应用程序的安全设置，包括JWT鉴权。
 *
 * 主要功能：
 * - 配置哪些接口需要鉴权，哪些不需要
 * - 集成JWT认证过滤器
 * - 禁用CSRF和CORS（因为使用JWT）
 * - 配置无状态会话管理
 *
 * @Configuration注解表示这是一个配置类，Spring会自动扫描并加载它。
 * @EnableWebSecurity注解启用Spring Security的Web安全支持。
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * JWT认证过滤器
     *
     * 用于验证请求中的JWT令牌
     */
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 安全过滤链 Bean
     *
     * 这个方法配置了HTTP安全设置，包括JWT鉴权规则。
     *
     * 鉴权规则：
     * - 不需要鉴权的接口：注册、登录、健康检测、监控端点、H2控制台、API文档
     * - 需要鉴权的接口：用户管理相关的所有其他接口
     *
     * @param http HttpSecurity对象，用于配置HTTP请求的安全性。
     * @return 配置好的SecurityFilterChain对象。
     * @throws Exception 配置过程中可能抛出的异常。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，因为我们使用JWT令牌
            .csrf(csrf -> csrf.disable())

            // 启用CORS，使用WebMvcConfig中的配置
            .cors(cors -> cors.configurationSource(request -> {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
                configuration.setAllowedOrigins(java.util.Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:3001"
                ));
                configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);
                return configuration;
            }))

            // 配置会话管理为无状态，因为我们使用JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 允许访问认证相关接口（不需要登录）
                .requestMatchers("/api/auth/**").permitAll()

                // 允许访问健康检测接口（不需要登录）
                .requestMatchers("/api/health/**").permitAll()

                // 允许访问Spring Boot Actuator监控端点（不需要登录）
                .requestMatchers("/actuator/**").permitAll()

                // 允许访问H2数据库控制台（开发环境）
                .requestMatchers("/h2-console/**").permitAll()

                // 允许访问API文档相关接口（不需要登录）
                .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 允许访问静态资源
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // 其他所有/api/**接口都需要认证
                .requestMatchers("/api/**").authenticated()

                // 其他请求允许访问
                .anyRequest().permitAll()
            )

            // 添加JWT认证过滤器，在用户名密码认证过滤器之前执行
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
