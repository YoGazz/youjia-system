package com.yoga.youjia.repository;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.common.enums.UserStatus;
import com.yoga.youjia.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository数据访问层测试
 * 
 * 使用@DataJpaTest注解进行JPA层的集成测试
 * 该注解会：
 * - 配置内存数据库（H2）
 * - 配置Spring Data JPA
 * - 提供TestEntityManager用于测试
 * - 自动回滚事务，确保测试之间不相互影响
 * 
 * 测试内容：
 * - 基本的CRUD操作
 * - 自定义查询方法
 * - 分页查询
 * - 条件查询
 */
@DataJpaTest
@DisplayName("UserRepository数据访问层测试")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    /**
     * 测试前的准备工作
     * 创建测试用的用户数据
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
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
    @DisplayName("保存用户应该成功")
    void testSaveUser() {
        // 保存用户
        User savedUser = userRepository.save(testUser);
        
        // 验证保存结果
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        
        // 从数据库中查询验证
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    @DisplayName("根据用户名查找用户应该成功")
    void testFindByUsername() {
        // 先保存用户
        entityManager.persistAndFlush(testUser);
        
        // 根据用户名查找
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        // 验证查找结果
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("根据不存在的用户名查找应该返回空")
    void testFindByUsernameNotFound() {
        // 查找不存在的用户名
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        
        // 验证结果为空
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("根据邮箱查找用户应该成功")
    void testFindByEmail() {
        // 先保存用户
        entityManager.persistAndFlush(testUser);
        
        // 根据邮箱查找
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // 验证查找结果
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("检查用户名是否存在应该正确")
    void testExistsByUsername() {
        // 先保存用户
        entityManager.persistAndFlush(testUser);
        
        // 检查存在的用户名
        assertTrue(userRepository.existsByUsername("testuser"));
        
        // 检查不存在的用户名
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("检查邮箱是否存在应该正确")
    void testExistsByEmail() {
        // 先保存用户
        entityManager.persistAndFlush(testUser);
        
        // 检查存在的邮箱
        assertTrue(userRepository.existsByEmail("test@example.com"));
        
        // 检查不存在的邮箱
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("条件分页查询应该正确")
    void testFindByConditions() {
        // 创建多个测试用户
        User user1 = createTestUser("user1", "user1@example.com", "用户一", com.yoga.youjia.common.enums.UserStatus.ACTIVE, com.yoga.youjia.common.enums.UserRole.DEVELOPER);
        User user2 = createTestUser("user2", "user2@example.com", "用户二", com.yoga.youjia.common.enums.UserStatus.INACTIVE, com.yoga.youjia.common.enums.UserRole.TESTER);
        User user3 = createTestUser("user3", "user3@example.com", "测试用户", com.yoga.youjia.common.enums.UserStatus.ACTIVE, com.yoga.youjia.common.enums.UserRole.DEVELOPER);
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(0, 10);
        
        // 测试按状态查询
        Page<User> enabledUsers = userRepository.findByConditions(null, UserStatus.ACTIVE, null, pageable);
        assertEquals(2, enabledUsers.getTotalElements());
        
        // 测试按角色查询
        Page<User> developers = userRepository.findByConditions(null, null, UserRole.DEVELOPER, pageable);
        assertEquals(2, developers.getTotalElements());
        
        // 测试按姓名模糊查询
        Page<User> usersWithName = userRepository.findByConditions("用户", null, null, pageable);
        assertEquals(2, usersWithName.getTotalElements());
        
        // 测试组合条件查询
        Page<User> specificUsers = userRepository.findByConditions("用户", UserStatus.ACTIVE, UserRole.DEVELOPER, pageable);
        assertEquals(1, specificUsers.getTotalElements());
        
        // 测试查询所有（所有条件为null）
        Page<User> allUsers = userRepository.findByConditions(null, null, null, pageable);
        assertEquals(3, allUsers.getTotalElements());
    }

    @Test
    @DisplayName("删除用户应该成功")
    void testDeleteUser() {
        // 先保存用户
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();
        
        // 确认用户存在
        assertTrue(userRepository.existsById(userId));
        
        // 删除用户
        userRepository.deleteById(userId);
        entityManager.flush();
        
        // 确认用户已被删除
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    @DisplayName("更新用户应该成功")
    void testUpdateUser() {
        // 先保存用户
        User savedUser = entityManager.persistAndFlush(testUser);
        
        // 更新用户信息
        savedUser.setRealName("更新后的姓名");
        savedUser.setStatus(com.yoga.youjia.common.enums.UserStatus.INACTIVE);
        User updatedUser = userRepository.save(savedUser);
        
        // 验证更新结果
        assertEquals("更新后的姓名", updatedUser.getRealName());
        assertEquals(com.yoga.youjia.common.enums.UserStatus.INACTIVE, updatedUser.getStatus());
        
        // 从数据库中重新查询验证
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertEquals("更新后的姓名", foundUser.getRealName());
        assertEquals(com.yoga.youjia.common.enums.UserStatus.INACTIVE, foundUser.getStatus());
    }

    /**
     * 创建测试用户的辅助方法
     */
    private User createTestUser(String username, String email, String realName, com.yoga.youjia.common.enums.UserStatus status, com.yoga.youjia.common.enums.UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        user.setRealName(realName);
        user.setEmail(email);
        user.setStatus(status);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
