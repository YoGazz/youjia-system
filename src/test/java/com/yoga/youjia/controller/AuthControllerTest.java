package com.yoga.youjia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.security.service.JWTService;
import com.yoga.youjia.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController认证控制器测试
 * 
 * 测试用户认证相关的HTTP接口：
 * - 用户注册接口
 * - 用户登录接口
 * - 参数验证
 * - JWT令牌生成
 * - 异常处理
 */
@WebMvcTest(controllers = {AuthController.class, com.yoga.youjia.common.GlobalExceptionHandler.class}, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@Import(com.yoga.youjia.config.AuthControllerTestConfig.class)
@DisplayName("AuthController认证控制器测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JWTService jwtService;

    private User testUser;

    /**
     * 测试前的准备工作
     * 创建测试用的用户数据
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setAvatar("avatar.jpg");
        testUser.setStatus(com.yoga.youjia.common.enums.UserStatus.ACTIVE);
        testUser.setRole(com.yoga.youjia.common.enums.UserRole.DEVELOPER);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("用户注册应该成功")
    void testRegister_Success() throws Exception {
        // 模拟Service返回注册成功的用户
        when(authService.register(any(User.class))).thenReturn(testUser);

        // 准备注册请求数据
        String registerRequest = """
                {
                    "username": "testuser",
                    "password": "password123",
                    "confirmPassword": "password123",
                    "email": "test@example.com",
                    "realName": "测试用户"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        // 验证Service方法被调用
        verify(authService, times(1)).register(any(User.class));
    }

    @Test
    @DisplayName("注册时密码不匹配应该返回400")
    void testRegister_PasswordMismatch() throws Exception {
        // 准备密码不匹配的注册请求
        String registerRequest = """
                {
                    "username": "testuser",
                    "password": "password123",
                    "confirmPassword": "differentpassword",
                    "email": "test@example.com"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码和确认密码不一致"));

        // 验证Service方法不应该被调用
        verify(authService, never()).register(any(User.class));
    }

    @Test
    @DisplayName("注册时参数验证失败应该返回400")
    void testRegister_ValidationFailed() throws Exception {
        // 准备无效的注册请求（用户名过短）
        String registerRequest = """
                {
                    "username": "ab",
                    "password": "password123",
                    "confirmPassword": "password123",
                    "email": "invalid-email"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 验证Service方法不应该被调用
        verify(authService, never()).register(any(User.class));
    }

    @Test
    @DisplayName("注册时用户名已存在应该返回400")
    void testRegister_UsernameExists() throws Exception {
        // 模拟Service抛出用户名已存在异常
        when(authService.register(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS));

        // 准备注册请求数据
        String registerRequest = """
                {
                    "username": "existinguser",
                    "password": "password123",
                    "confirmPassword": "password123",
                    "email": "test@example.com"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        // 验证Service方法被调用
        verify(authService, times(1)).register(any(User.class));
    }

    @Test
    @DisplayName("用户登录应该成功")
    void testLogin_Success() throws Exception {
        // 模拟Service返回登录成功的用户
        when(authService.login("testuser", "password123")).thenReturn(testUser);
        
        // 模拟JWT服务生成令牌
        when(jwtService.generateToken("testuser")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9...");
        when(jwtService.getExpirationTime()).thenReturn(86400000L); // 24小时

        // 准备登录请求数据
        String loginRequest = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.token.authorization").value("Bearer"))
                .andExpect(jsonPath("$.data.token.access_token").exists())
                .andExpect(jsonPath("$.data.token.expires_in").value(86400));

        // 验证Service方法被调用
        verify(authService, times(1)).login("testuser", "password123");
        verify(jwtService, times(1)).generateToken("testuser");
    }

    @Test
    @DisplayName("登录时参数验证失败应该返回400")
    void testLogin_ValidationFailed() throws Exception {
        // 准备无效的登录请求（用户名为空）
        String loginRequest = """
                {
                    "username": "",
                    "password": "password123"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 验证Service方法不应该被调用
        verify(authService, never()).login(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("登录时用户名或密码错误应该返回400")
    void testLogin_InvalidCredentials() throws Exception {
        // 模拟Service抛出登录失败异常
        when(authService.login("testuser", "wrongpassword"))
                .thenThrow(new BusinessException(ErrorCode.PASSWORD_INVALID));

        // 准备登录请求数据
        String loginRequest = """
                {
                    "username": "testuser",
                    "password": "wrongpassword"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码错误"));

        // 验证Service方法被调用，但JWT服务不应该被调用
        verify(authService, times(1)).login("testuser", "wrongpassword");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("登录时用户被禁用应该返回400")
    void testLogin_UserDisabled() throws Exception {
        // 模拟Service抛出用户被禁用异常
        when(authService.login("testuser", "password123"))
                .thenThrow(new BusinessException(ErrorCode.USER_DISABLED));

        // 准备登录请求数据
        String loginRequest = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户已被停用"));

        // 验证Service方法被调用，但JWT服务不应该被调用
        verify(authService, times(1)).login("testuser", "password123");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("注册请求缺少必填字段应该返回400")
    void testRegister_MissingRequiredFields() throws Exception {
        // 准备缺少必填字段的注册请求
        String registerRequest = """
                {
                    "username": "testuser"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 验证Service方法不应该被调用
        verify(authService, never()).register(any(User.class));
    }

    @Test
    @DisplayName("登录请求缺少必填字段应该返回400")
    void testLogin_MissingRequiredFields() throws Exception {
        // 准备缺少必填字段的登录请求
        String loginRequest = """
                {
                    "username": "testuser"
                }
                """;

        // 执行POST请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 验证Service方法不应该被调用
        verify(authService, never()).login(anyString(), anyString());
    }
}
