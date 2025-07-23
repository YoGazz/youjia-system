package com.yoga.youjia.service;

import com.yoga.youjia.common.exception.DataConflictException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import com.yoga.youjia.dto.request.UserQueryDTO;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户服务类
 *
 * 负责处理用户管理相关的业务逻辑，包括：
 * - 用户信息查询
 * - 用户信息更新
 * - 用户状态管理
 * - 用户删除
 * - 分页查询等
 *
 * 在Spring MVC架构中，Service层位于Controller层和Repository层之间，
 * 主要负责业务逻辑的处理和事务管理
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", String.valueOf(userId)));
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新后的用户信息
     * @throws ResourceNotFoundException 用户不存在时抛出
     * @throws DataConflictException 数据冲突时抛出（如用户名或邮箱重复）
     */
    @Transactional
    public User updateUser(User user) {
        logger.info("开始更新用户: {}", user.getId());

        // 先检查用户是否存在
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", String.valueOf(user.getId())));

        // 检查邮箱是否被其他用户使用
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new DataConflictException("邮箱", user.getEmail());
            }
        }

        // 检查用户名是否被其他用户使用
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new DataConflictException("用户名", user.getUsername());
            }
        }

        // 更新字段（只更新非空字段）
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
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
     * 条件分页查询用户
     *
     * @param queryDTO 查询条件
     * @return 分页用户列表
     */
    public Page<User> getUsersByConditions(UserQueryDTO queryDTO) {
        // 创建分页请求，页码从0开始，按ID降序排序
        PageRequest pageRequest = PageRequest.of(
                queryDTO.getPage() - 1,
                queryDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        // 调用Repository的条件查询方法
        return userRepository.findByConditions(
                queryDTO.getRealName(),
                queryDTO.getStatus(),
                queryDTO.getRole(),
                pageRequest
        );
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    @Transactional
    public void deleteUser(Long userId) {
        // 先检查用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("用户", String.valueOf(userId));
        }
        
        userRepository.deleteById(userId);
        logger.info("删除用户成功: userId={}", userId);
    }

    /**
     * 分页查询用户
     *
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    public List<User> getUsersByPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return userRepository.findAll(pageRequest).getContent();
    }

    /**
     * 获取所有用户
     *
     * @return 所有用户列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户名为 " + username + " 的用户"));
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 新状态
     * @return 更新后的用户信息
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    @Transactional
    public User updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", String.valueOf(userId)));
        
        user.setStatus(status);
        User savedUser = userRepository.save(user);
        
        logger.info("更新用户状态成功: userId={}, status={}", userId, status);
        return savedUser;
    }
}
