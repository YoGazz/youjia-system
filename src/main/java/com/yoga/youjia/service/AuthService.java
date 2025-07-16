package com.yoga.youjia.service;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 * 
 * 这个类专门负责处理用户认证相关的业务逻辑，包括：
 * - 用户注册
 * - 用户登录
 * - 密码验证
 * 
 * 为什么要单独创建AuthService？
 * 1. 职责分离：认证逻辑和用户管理逻辑分开
 * 2. 代码组织：让每个Service类的职责更加明确
 * 3. 便于维护：认证相关的功能集中管理
 * 4. 便于扩展：后续可以添加JWT、OAuth等认证方式
 */
@Service  // 告诉Spring这是一个服务类
public class AuthService {

    /**
     * 用户数据访问对象
     * 
     * 用于操作用户数据，比如查询用户、保存用户等
     */
    @Autowired  // 让Spring自动注入UserRepository
    private UserRepository userRepository;

    /**
     * 用户注册方法
     * 
     * 处理用户注册的完整流程：
     * 1. 检查用户名是否已存在
     * 2. 检查邮箱是否已存在
     * 3. 设置用户默认信息
     * 4. 保存用户到数据库
     * 
     * @param user 要注册的用户信息
     * @return User 注册成功后的用户对象
     * @throws IllegalArgumentException 当用户名或邮箱已存在时抛出
     */
    public User register(User user) {
        // 第一步：检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 第二步：检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 第三步：设置用户默认信息
        user.setStatus(User.Status.ENABLE.getDescription());  // 设置状态为启用
        user.setRole(User.Roles.USER.getDescription());       // 设置角色为普通用户
        user.setCreatedAt(java.time.LocalDateTime.now());     // 设置创建时间

        // 第四步：保存用户到数据库
        return userRepository.save(user);
    }

    /**
     * 用户登录方法
     * 
     * 验证用户的登录凭据（用户名和密码）
     * 
     * 登录流程：
     * 1. 根据用户名查找用户
     * 2. 检查用户是否存在
     * 3. 验证密码是否正确
     * 4. 检查用户状态是否为启用
     * 5. 返回登录成功的用户信息
     * 
     * @param username 用户名
     * @param password 密码
     * @return User 登录成功的用户对象
     * @throws IllegalArgumentException 当登录失败时抛出
     */
    public User login(String username, String password) {
        // 第一步：根据用户名查找用户
        User user = userRepository.findByUsername(username)
                .orElse(null);  // 如果找不到用户，返回null

        // 第二步：检查用户是否存在
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 第三步：验证密码是否正确
        // 注意：这里是简单的明文密码比较
        // 实际项目中应该使用加密密码比较（如BCrypt）
        if (!password.equals(user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 第四步：检查用户状态是否为启用
        if (!User.Status.ENABLE.getDescription().equals(user.getStatus())) {
            throw new IllegalArgumentException("用户账户已被禁用");
        }

        // 第五步：返回登录成功的用户信息
        // 注意：返回前应该清除密码信息，避免泄露
        user.setPassword(null);  // 清除密码，不返回给前端
        return user;
    }

    /**
     * 根据邮箱登录
     * 
     * 支持用户使用邮箱进行登录
     * 
     * @param email 邮箱地址
     * @param password 密码
     * @return User 登录成功的用户对象
     * @throws IllegalArgumentException 当登录失败时抛出
     */
    public User loginByEmail(String email, String password) {
        // 根据邮箱查找用户
        User user = userRepository.findByEmail(email)
                .orElse(null);

        // 检查用户是否存在
        if (user == null) {
            throw new IllegalArgumentException("邮箱或密码错误");
        }

        // 验证密码
        if (!password.equals(user.getPassword())) {
            throw new IllegalArgumentException("邮箱或密码错误");
        }

        // 检查用户状态
        if (!User.Status.ENABLE.getDescription().equals(user.getStatus())) {
            throw new IllegalArgumentException("用户账户已被禁用");
        }

        // 清除密码并返回用户信息
        user.setPassword(null);
        return user;
    }

    /**
     * 检查用户名是否可用
     * 
     * 用于注册时的实时验证
     * 
     * @param username 要检查的用户名
     * @return boolean true表示可用，false表示已存在
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否可用
     * 
     * 用于注册时的实时验证
     * 
     * @param email 要检查的邮箱
     * @return boolean true表示可用，false表示已存在
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // TODO: 后续可以添加的功能
    // - 密码加密和验证
    // - JWT令牌生成和验证
    // - 找回密码功能
    // - 邮箱验证功能
    // - 登录日志记录
    // - 登录失败次数限制
}
