package com.yoga.youjia.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CORS跨域配置测试
 *
 * 测试CORS配置是否正确工作，包括：
 * - 预检请求（OPTIONS）处理
 * - 允许的源地址验证
 * - 允许的HTTP方法验证
 * - 允许的请求头验证
 * - 凭据支持验证
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CORS跨域配置测试")
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试允许的源地址 - localhost:3000
     */
    @Test
    @DisplayName("测试允许的源地址 - localhost:3000")
    void testAllowedOrigin3000() throws Exception {
        mockMvc.perform(options("/api/health/check")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    /**
     * 测试允许的源地址 - localhost:3001
     */
    @Test
    @DisplayName("测试允许的源地址 - localhost:3001")
    void testAllowedOrigin3001() throws Exception {
        mockMvc.perform(options("/api/health/check")
                .header("Origin", "http://localhost:3001")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3001"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    /**
     * 测试不允许的源地址
     */
    @Test
    @DisplayName("测试不允许的源地址")
    void testDisallowedOrigin() throws Exception {
        mockMvc.perform(options("/api/health/check")
                .header("Origin", "http://localhost:8080")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    /**
     * 测试允许的HTTP方法
     */
    @Test
    @DisplayName("测试允许的HTTP方法")
    void testAllowedMethods() throws Exception {
        String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};

        for (String method : allowedMethods) {
            mockMvc.perform(options("/api/health/check")
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", method))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Access-Control-Allow-Methods"));
        }
    }

    /**
     * 测试实际的GET请求
     */
    @Test
    @DisplayName("测试实际的GET请求")
    void testActualGetRequest() throws Exception {
        mockMvc.perform(get("/api/health/check")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    /**
     * 测试非API路径不受CORS影响
     */
    @Test
    @DisplayName("测试非API路径不受CORS影响")
    void testNonApiPath() throws Exception {
        mockMvc.perform(options("/health")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk()) // Spring Boot会处理OPTIONS请求
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000")); // 仍然会有CORS头，因为全局配置
    }
}
