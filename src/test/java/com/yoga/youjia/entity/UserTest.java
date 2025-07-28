package com.yoga.youjia.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User实体类单元测试
 * 
 * 测试User实体类的各种功能：
 * - 基本属性的getter/setter
 * - 数据验证注解的有效性
 * - 枚举类型的正确性
 * - 对象的相等性和哈希码
 * 
 * 使用JUnit 5进行测试，@DisplayName提供中文测试描述
 */
@DisplayName("User实体类测试")
class UserTest {

    private Validator validator;
    private User user;

    /**
     * 测试前的准备工作
     * 初始化验证器和测试用的User对象
     */
    @BeforeEach
    void setUp() {
        // 初始化Bean验证器
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // 创建有效的测试用户对象
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRealName("测试用户");
        user.setEmail("test@example.com");
        user.setAvatar("avatar.jpg");
        user.setStatus(com.yoga.youjia.common.enums.UserStatus.ACTIVE);
        user.setRole(com.yoga.youjia.common.enums.UserRole.DEVELOPER);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建有效用户对象应该成功")
    void testCreateValidUser() {
        // 验证用户对象的基本属性
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("测试用户", user.getRealName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("avatar.jpg", user.getAvatar());
        assertEquals(com.yoga.youjia.common.enums.UserStatus.ACTIVE, user.getStatus());
        assertEquals(com.yoga.youjia.common.enums.UserRole.DEVELOPER, user.getRole());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("用户名验证测试")
    void testUsernameValidation() {
        // 测试用户名为空的情况
        user.setUsername("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名不能为空")));

        // 测试用户名过短的情况
        user.setUsername("ab");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名长度必须在3到20之间")));

        // 测试用户名过长的情况
        user.setUsername("a".repeat(21));
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名长度必须在3到20之间")));

        // 测试有效用户名
        user.setUsername("validuser");
        violations = validator.validate(user);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("邮箱验证测试")
    void testEmailValidation() {
        // 测试邮箱为空的情况
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("邮箱不能为空")));

        // 测试邮箱格式错误的情况
        user.setEmail("invalid-email");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("邮箱格式不正确")));

        // 测试有效邮箱
        user.setEmail("valid@example.com");
        violations = validator.validate(user);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("用户角色枚举测试")
    void testUserRolesEnum() {
        // 测试所有角色枚举值
        assertEquals("系统管理员", com.yoga.youjia.common.enums.UserRole.ADMIN.getName());
        assertEquals("ADMIN", com.yoga.youjia.common.enums.UserRole.ADMIN.getCode());
        
        assertEquals("项目经理", com.yoga.youjia.common.enums.UserRole.PROJECT_MANAGER.getName());
        assertEquals("PROJECT_MANAGER", com.yoga.youjia.common.enums.UserRole.PROJECT_MANAGER.getCode());
        
        assertEquals("测试工程师", com.yoga.youjia.common.enums.UserRole.TESTER.getName());
        assertEquals("TESTER", com.yoga.youjia.common.enums.UserRole.TESTER.getCode());
        
        assertEquals("开发工程师", com.yoga.youjia.common.enums.UserRole.DEVELOPER.getName());
        assertEquals("DEVELOPER", com.yoga.youjia.common.enums.UserRole.DEVELOPER.getCode());
    }

    @Test
    @DisplayName("根据枚举名称获取中文名称测试")
    void testGetNameByEnumName() {
        // 测试有效的枚举名称
        assertEquals("系统管理员", com.yoga.youjia.common.enums.UserRole.valueOf("ADMIN").getName());
        assertEquals("开发工程师", com.yoga.youjia.common.enums.UserRole.valueOf("DEVELOPER").getName());
    }

    @Test
    @DisplayName("用户状态枚举测试")
    void testUserStatusEnum() {
        // 测试状态枚举值
        assertEquals("ACTIVE", com.yoga.youjia.common.enums.UserStatus.ACTIVE.name());
        assertEquals("INACTIVE", com.yoga.youjia.common.enums.UserStatus.INACTIVE.name());
    }

    @Test
    @DisplayName("完整的用户对象验证测试")
    void testCompleteUserValidation() {
        // 创建完全有效的用户对象
        User validUser = new User();
        validUser.setUsername("validuser");
        validUser.setPassword("validpassword");
        validUser.setEmail("valid@example.com");
        validUser.setRealName("有效用户");
        
        // 验证应该通过
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty(), "有效用户对象不应该有验证错误");
    }
}
