package com.yoga.youjia.service;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.repository.UserRepository;
import com.yoga.youjia.common.enums.UserStatus;
import com.yoga.youjia.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService认证服务测试
 * 
 * 测试用户认证相关的业务逻辑：
 * - 用户注册功能
 * - 用户登录功能
 * - 密码验证
 * - 用户名和邮箱唯一性检查
 * 
 * 使用Mockito模拟依赖对象，专注于业务逻辑测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService认证服务测试")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

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
    void testRegister_Success() {
        // 准备注册用户数据
        User registerUser = new User();
        registerUser.setUsername("newuser");
        registerUser.setPassword("password123");
        registerUser.setEmail("newuser@example.com");
        registerUser.setRealName("新用户");

        // 模拟Repository和PasswordEncoder行为
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            savedUser.setCreatedAt(LocalDateTime.now());
            return savedUser;
        });

        // 调用注册方法
        User result = authService.register(registerUser);

        // 验证结果
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("新用户", result.getRealName());
        assertEquals(UserStatus.PENDING, result.getStatus());
        assertEquals(UserRole.USER, result.getRole());
        assertNotNull(result.getCreatedAt());

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("用户名已存在时注册应该失败")
    void testRegister_UsernameExists() {
        // 准备注册用户数据
        User registerUser = new User();
        registerUser.setUsername("existinguser");
        registerUser.setPassword("password123");
        registerUser.setEmail("new@example.com");

        // 模拟用户名已存在
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerUser);
        });

        assertEquals("用户名已存在", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("existinguser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("邮箱已存在时注册应该失败")
    void testRegister_EmailExists() {
        // 准备注册用户数据
        User registerUser = new User();
        registerUser.setUsername("newuser");
        registerUser.setPassword("password123");
        registerUser.setEmail("existing@example.com");

        // 模拟邮箱已存在
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerUser);
        });

        assertEquals("邮箱已存在", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("用户登录应该成功")
    void testLogin_Success() {
        // 模拟数据库中的加密密码
        String encodedPassword = "$2a$10$encodedPassword";
        testUser.setPassword(encodedPassword);

        // 模拟Repository和PasswordEncoder行为
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", encodedPassword)).thenReturn(true);

        // 调用登录方法
        User result = authService.login("testuser", "password123");

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", encodedPassword);
    }

    @Test
    @DisplayName("用户名不存在时登录应该失败")
    void testLogin_UserNotFound() {
        // 模拟用户不存在
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("nonexistent", "password123");
        });

        assertEquals("用户名或密码错误", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("密码错误时登录应该失败")
    void testLogin_WrongPassword() {
        // 模拟数据库中的加密密码
        String encodedPassword = "$2a$10$encodedPassword";
        testUser.setPassword(encodedPassword);

        // 模拟Repository和PasswordEncoder行为
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", encodedPassword)).thenReturn(false);

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("testuser", "wrongpassword");
        });

        assertEquals("用户名或密码错误", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", encodedPassword);
    }

    @Test
    @DisplayName("用户状态为禁用时登录应该失败")
    void testLogin_UserDisabled() {
        // 设置用户状态为禁用
        testUser.setStatus(com.yoga.youjia.common.enums.UserStatus.INACTIVE);
        String encodedPassword = "$2a$10$encodedPassword";
        testUser.setPassword(encodedPassword);

        // 模拟Repository和PasswordEncoder行为
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", encodedPassword)).thenReturn(true);

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("testuser", "password123");
        });

        assertEquals("用户账户已被停用", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", encodedPassword);
    }

    @Test
    @DisplayName("注册时应该正确设置用户默认信息")
    void testRegister_DefaultValues() {
        // 准备最小注册用户数据
        User registerUser = new User();
        registerUser.setUsername("newuser");
        registerUser.setPassword("password123");
        registerUser.setEmail("newuser@example.com");
        // 不设置realName，测试null处理

        // 模拟Repository和PasswordEncoder行为
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            savedUser.setCreatedAt(LocalDateTime.now());
            return savedUser;
        });

        // 调用注册方法
        User result = authService.register(registerUser);

        // 验证默认值设置
        assertEquals(UserStatus.PENDING, result.getStatus());
        assertEquals(UserRole.USER, result.getRole());
        assertNull(result.getRealName()); // realName为null时应该保持null
        assertNotNull(result.getCreatedAt());

        // 验证密码被加密
        assertEquals("$2a$10$encodedPassword", result.getPassword());
    }

    @Test
    @DisplayName("注册时应该正确处理realName的空白字符")
    void testRegister_RealNameTrimming() {
        // 准备注册用户数据，realName包含空白字符
        User registerUser = new User();
        registerUser.setUsername("newuser");
        registerUser.setPassword("password123");
        registerUser.setEmail("newuser@example.com");
        registerUser.setRealName("  测试用户  ");

        // 模拟Repository和PasswordEncoder行为
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 调用注册方法
        User result = authService.register(registerUser);

        // 验证realName被正确处理（去除首尾空格）
        assertEquals("测试用户", result.getRealName());
    }
}
