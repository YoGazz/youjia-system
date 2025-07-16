package com.yoga.youjia.controller;
import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.dto.request.UserQueryDTO;
import com.yoga.youjia.dto.response.PageResponseDTO;
import com.yoga.youjia.dto.response.UserResponseDTO;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制器类
 *
 * 这个类负责处理用户管理相关的网络请求，包括：
 * - 获取用户信息
 * - 更新用户信息
 * - 用户状态管理
 * - 用户删除
 * - 分页查询等
 */
@Tag(name = "用户管理", description = "用户信息的增删改查接口") // 用于Swagger文档生成
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 用户服务类
     * 通过依赖注入获取UserService实例，进行用户相关操作
     */
    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     * 如果用户不存在或查询失败，返回错误响应
     * @PathVaiable 注解用于从URL路径中获取参数
     */
    @Operation(summary = "获取用户信息", description = "根据用户ID查询用户信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "查询成功，返回用户信息"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401", description = "用户不存在或查询失败"
                    )
            }
    )
    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable Long userId){
        try {
            // 调用UserService获取用户信息
            User user = userService.getUserById(userId);
            // 将User实体转换为UserResponseDTO
            UserResponseDTO userResponseDTO = UserResponseDTO.from(user);
            // 返回成功响应
            return ApiResponse.success(userResponseDTO);
        }catch (Exception e){
            return ApiResponse.error("401","用户不存在或查询失败");
        }
    }

    /**
     * 更新用户信息
     * 根据用户ID更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @Operation 注解用于描述API操作
     * @ApiResponses 注解用于描述API响应
     * @PutMapping 注解用于处理PUT请求
     * @PathVariable 注解用于从URL路径中获取参数
     * @RequestBody 注解用于获取请求体中的数据
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "更新成功，返回更新后的用户信息"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500", description = "更新用户信息失败")
            }
    )
    @PutMapping("/{userId}")
    public ApiResponse<UserResponseDTO> updateUser(@PathVariable Long userId, @RequestBody User user){
        try{
            // 设置用户ID，确保更新的是正确的用户
            user.setId(userId);
            // 调用UserService更新用户信息
            User updateUser = userService.updateUser(user);
            // 将更新后的User实体转换为UserResponseDTO
            UserResponseDTO userResponseDTO = UserResponseDTO.from(updateUser);
            return ApiResponse.success(userResponseDTO);
        }catch (Exception e){
            return ApiResponse.error("500", "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 条件分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 分页用户列表
     */
    @Operation(summary = "条件分页查询用户", description = "根据姓名、状态、角色等条件查询用户列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "查询失败")
    })
    @PostMapping("/search")  // 修改为POST请求，并使用专用路径
    public ApiResponse<PageResponseDTO<UserResponseDTO>> getUsersByConditions(@RequestBody UserQueryDTO queryDTO) {  // 添加@RequestBody
        try {
            // 调用Service查询用户
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

            return ApiResponse.success(pageResponse);
        } catch (Exception e) {
            return ApiResponse.error("500", "查询用户列表失败: " + e.getMessage());
        }
    }

}
