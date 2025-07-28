package com.yoga.youjia.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 测试配置类
 * 
 * 为单元测试提供必要的Bean配置，避免在测试中依赖完整的Spring上下文
 * 
 * 主要功能：
 * - 提供测试专用的Bean配置
 * - 覆盖生产环境的某些配置
 * - 简化测试环境的依赖关系
 * 
 * @TestConfiguration注解表示这是一个测试配置类
 * 只在测试环境中生效，不会影响生产环境
 */
@TestConfiguration
public class TestConfig {

    /**
     * 密码编码器Bean
     * 
     * 为测试提供密码加密功能
     * @Primary注解确保在测试中优先使用这个Bean
     * 
     * @return BCrypt密码编码器实例
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
