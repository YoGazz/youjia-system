package com.yoga.youjia.service;

import com.yoga.youjia.common.enums.UserRole;
import com.yoga.youjia.common.enums.UserStatus;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 *
 * 这个类专门负责处理用户认证相关的业务逻辑，包括：
 * - 用户注册
 * - 用户登录
 * - 密码验证
 * 支持新的用户角色权限系统
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // 第三步：加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 第四步：设置用户默认信息
        user.setStatus(UserStatus.PENDING); // 新注册用户需要激活
        user.setRole(UserRole.USER);         // 默认为普通用户
        
        // 处理真实姓名
        if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            user.setRealName(user.getRealName().trim());
        } else {
            user.setRealName(null);
        }

        // 第五步：保存用户到数据库
        return userRepository.save(user);
    }

    /**
     * 用户登录方法
     *
     * 验证用户的登录凭据（用户名和密码）
     *
     * @param username 用户名
     * @param password 密码
     * @return User 登录成功的用户对象
     * @throws IllegalArgumentException 当登录失败时抛出
     */
    public User login(String username, String password) {
        // 第一步：根据用户名查找用户
        User user = userRepository.findByUsername(username)
                .orElse(null);

        // 第二步：检查用户是否存在
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 第三步：验证密码是否正确
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 增加密码错误次数
            user.incrementPasswordErrorCount();
            userRepository.save(user);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 第四步：检查用户状态
        if (!user.canLogin()) {
            String statusMessage = getStatusMessage(user.getStatus());
            throw new IllegalArgumentException(statusMessage);
        }

        // 第五步：记录登录信息
        user.recordLoginInfo(null); // TODO: 获取实际IP地址
        userRepository.save(user);

        // 第六步：返回登录成功的用户信息
        user.setPassword(null); // 清除密码，不返回给前端
        return user;
    }
    
    /**
     * 获取用户状态对应的提示信息
     */
    private String getStatusMessage(UserStatus status) {
        if (status == null) {
            return "用户状态异常";
        }
        
        switch (status) {
            case INACTIVE:
                return "用户账户已被停用";
            case LOCKED:
                return "用户账户已被锁定，请联系管理员";
            case PENDING:
                return "用户账户待激活，请联系管理员";
            case DELETED:
                return "用户账户已删除";
            default:
                return "用户账户状态异常";
        }
    }
}
