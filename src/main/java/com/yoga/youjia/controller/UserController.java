package com.yoga.youjia.controller;
import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.dto.response.UserResponseDTO;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
                            responseCode = "200",
                            description = "查询成功，返回用户信息"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "用户不存在或查询失败"
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
}
