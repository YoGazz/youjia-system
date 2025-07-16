package com.yoga.youjia.service;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户服务类
 *
 * 这个类负责处理用户管理相关的业务逻辑，包括：
 * - 用户信息查询
 * - 用户信息更新
 * - 用户状态管理
 * - 用户删除
 * - 分页查询等
 * * 通过依赖注入获取UserRepository实例，进行数据访问
 * 在Spring MVC架构中，Service层位于Controller层和Repository层之间，
 * 主要负责业务逻辑的处理和事务管理
 */
@Service  // @Service注解的作用：
          // 1. 标识这是一个Spring服务组件（Service层）
          // 2. Spring容器会自动扫描并创建这个类的实例（Bean）
          // 3. 可以被其他组件（如Controller）通过依赖注入使用
          // 4. 是@Component注解的特化版本，专门用于Service层
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户信息
     *
     * 根据用户ID查询用户信息
     * 如果用户不存在，抛出异常
     * orElseThrow方法用于在查询不到用户时抛出异常
     * IllegalArgumentException是一个运行时异常，
     * @param UserId 用户ID
     * @return 用户信息
     */
    public User getUserById(Long UserId){
        return userRepository.findById(UserId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 更新用户信息
     *
     * 根据用户ID更新用户信息
     *
     * @param user 用户信息
     * @return 更新后的用户信息
     */

    @Transactional
    public User updateUser(User user) {
        logger.info("开始更新用户: {}", user.getId());

        // 先检查用户是否存在
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查邮箱是否被其他用户使用
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("邮箱已被其他用户使用");
            }
        }

        // 检查用户名是否被其他用户使用
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("用户名已被其他用户使用");
            }
        }

        // 更新字段（只更新非空字段）
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }

        logger.info("准备保存用户: {}", existingUser.getId());
        User savedUser = userRepository.save(existingUser);
        logger.info("用户保存成功: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * 删除用户
     *
     * 根据用户ID删除用户
     *
     * @param userId 用户ID
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    /**
     * 分页查询用户
     *
     * 根据页码和每页大小查询用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    public List<User> getUsersByPage(int page, int size){
        // PageRequest.of(page-1, size) 方法创建一个分页请求对象
        PageRequest pageRequest = PageRequest.of(page-1, size);
        // 调用UserRepository的findAll方法，传入分页请求对象
        return userRepository.findAll(pageRequest).getContent();
    }

    /**
     * 获取所有用户
     *
     * @return 所有用户列表
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 新状态
     */
    public User updateUserStatus(Long userId, String status){
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new IllegalArgumentException("用户不存在"));
        user.setStatus(status);
        return userRepository.save(user);
    }
}
