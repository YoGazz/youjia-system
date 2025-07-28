package com.yoga.youjia.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * AuthController测试专用配置类
 * 
 * 为AuthController测试提供必要的Bean配置，避免安全配置依赖问题
 */
@TestConfiguration
public class AuthControllerTestConfig {

    /**
     * 测试用的UserDetailsService
     * 
     * 为测试环境提供用户详情服务，避免JWTAuthenticationFilter依赖问题
     * 
     * @return 内存中的用户详情服务
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();
        
        return new InMemoryUserDetailsManager(user);
    }
}