package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import com.yoga.youjia.dto.request.UserQueryDTO;
import com.yoga.youjia.dto.response.PageResponseDTO;
import com.yoga.youjia.dto.response.UserResponseDTO;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 *
 * 负责用户管理相关的HTTP请求处理，包括：
 * - 获取用户信息
 * - 更新用户信息
 * - 条件查询用户
 * - 删除用户
 * 
 * 采用RESTful设计规范：
 * - GET /api/users/{id} - 获取指定用户信息
 * - PUT /api/users/{id} - 更新指定用户信息
 * - DELETE /api/users/{id} - 删除指定用户
 * - POST /api/users/search - 条件查询用户列表
 */
@Tag(name = "用户管理", description = "用户信息的增删改查接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "根据用户ID查询用户详细信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable Long userId) {
        logger.info("查询用户信息: userId={}", userId);
        
        try {
            User user = userService.getUserById(userId);
            UserResponseDTO userResponseDTO = UserResponseDTO.from(user);
            
            logger.info("查询用户成功: userId={}, username={}", user.getId(), user.getUsername());
            return ApiResponse.success(userResponseDTO, "查询成功");
            
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("用户", String.valueOf(userId));
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "用户不存在"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "数据冲突（如用户名已存在）")
    })
    @PutMapping("/{userId}")
    public ApiResponse<UserResponseDTO> updateUser(
            @PathVariable Long userId, 
            @Valid @RequestBody User user) {
        logger.info("更新用户信息: userId={}", userId);
        
        // 设置用户ID，确保更新的是正确的用户
        user.setId(userId);
        
        User updatedUser = userService.updateUser(user);
        UserResponseDTO userResponseDTO = UserResponseDTO.from(updatedUser);
        
        logger.info("更新用户成功: userId={}, username={}", updatedUser.getId(), updatedUser.getUsername());
        return ApiResponse.success(userResponseDTO, "更新成功");
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        logger.info("删除用户: userId={}", userId);
        
        // 先检查用户是否存在
        userService.getUserById(userId);
        
        userService.deleteUser(userId);
        
        logger.info("删除用户成功: userId={}", userId);
        return ApiResponse.success("删除成功");
    }

    /**
     * 条件分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 分页用户列表
     */
    @Operation(summary = "条件查询用户", description = "根据姓名、状态、角色等条件分页查询用户列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "查询成功")
    })
    @PostMapping("/search")
    public ApiResponse<PageResponseDTO<UserResponseDTO>> searchUsers(
            @Valid @RequestBody UserQueryDTO queryDTO) {
        logger.info("条件查询用户: page={}, size={}, realName={}, status={}, role={}", 
                   queryDTO.getPage(), queryDTO.getSize(), queryDTO.getRealName(), 
                   queryDTO.getStatus(), queryDTO.getRole());
        
        Page<User> userPage = userService.getUsersByConditions(queryDTO);

        // 转换为DTO
        List<UserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());

        // 构建分页响应
        PageResponseDTO<UserResponseDTO> pageResponse = new PageResponseDTO<>(
                userDTOs,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber() + 1,
                userPage.getSize()
        );

        logger.info("查询用户列表成功: 返回{}条记录，共{}页", 
                   userDTOs.size(), userPage.getTotalPages());
        return ApiResponse.success(pageResponse, "查询成功");
    }

    /**
     * 获取所有用户列表
     *
     * @return 所有用户列表
     */
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户的列表")
    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        logger.info("获取所有用户列表");
        
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
        
        logger.info("获取所有用户成功: 共{}个用户", userDTOs.size());
        return ApiResponse.success(userDTOs, "查询成功");
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 新状态
     * @return 更新结果
     */
    @Operation(summary = "更新用户状态", description = "更新指定用户的状态")
    @PatchMapping("/{userId}/status")
    public ApiResponse<UserResponseDTO> updateUserStatus(
            @PathVariable Long userId, 
            @RequestParam String status) {
        logger.info("更新用户状态: userId={}, status={}", userId, status);
        
        User updatedUser = userService.updateUserStatus(userId, status);
        UserResponseDTO userResponseDTO = UserResponseDTO.from(updatedUser);
        
        logger.info("更新用户状态成功: userId={}, status={}", userId, status);
        return ApiResponse.success(userResponseDTO, "状态更新成功");
    }
}
