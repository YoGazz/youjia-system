package com.yoga.youjia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类
 * 这个类用于配置应用程序的安全设置。
 * @Configuration注解表示这是一个配置类，Spring会自动扫描并加载它。
 */
@Configuration
public class SecurityConfig {

    /**
     * 密码编码器 Bean
     * @Bean注解表示这个方法会返回一个Bean对象，
     * 这个Bean会被Spring管理。
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

}
