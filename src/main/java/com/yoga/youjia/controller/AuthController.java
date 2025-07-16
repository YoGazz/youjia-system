package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.dto.request.LoginRequestDTO;
import com.yoga.youjia.dto.request.RegisterRequestDTO;
import com.yoga.youjia.dto.response.LoginResponseDTO;
import com.yoga.youjia.dto.response.UserResponseDTO;
import com.yoga.youjia.entity.User;
import com.yoga.youjia.security.service.JWTService;
import com.yoga.youjia.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.UUID;

/**
 * 认证控制器类（简化版）
 *
 * 这个类专门处理用户认证相关的HTTP请求，包括：
 * - 用户注册
 * - 用户登录
 *
 * 为什么要单独创建AuthController？
 * 1. 职责分离：认证功能和用户管理功能分开
 * 2. URL组织：认证相关的接口统一放在/api/auth下
 * 3. 便于维护：认证相关的接口集中管理
 * 4. 符合RESTful设计：不同的资源使用不同的控制器
 */
@Tag(name="用户认证", description = "用户注册，登录等认证相关接口") // 用于Swagger文档生成
@RestController                 // 告诉Spring这是一个REST控制器
@RequestMapping("/api/auth")    // 认证相关接口的基础路径
public class AuthController {

    /**
     * 认证服务对象
     *
     * 用于处理认证相关的业务逻辑
     */
    @Autowired  // 让Spring自动注入AuthService
    private AuthService authService;

    /**
     * JWT服务对象
     *
     * 用于生成和验证JWT令牌
     */
    @Autowired  // 让Spring自动注入JWTService
    private JWTService jwtService;

    /**
     * 用户注册接口
     *
     * 处理用户注册请求，验证输入数据，创建新用户，并返回注册结果
     *
     * @param registerRequestDTO 注册请求数据传输对象
     * @return ApiResponse<UserResponseDTO> 包含注册结果的响应对象
     * @Operation 用于Swagger文档生成，描述接口功能和参数
     * @PostMapping 处理POST请求，完整地址是/api/auth/register
     * @Valid 用于验证请求体中的数据
     * @RequestBody 用于将请求体转换为RegisterRequestDTO对象
     * @ApiResponses 用于描述可能的响应状态码和含义
     */
    @Operation(summary = "用户注册接口", description = "处理用户注册请求，验证输入数据，创建新用户，并返回注册结果")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "系统错误")
    })
    @PostMapping("/register")   // 处理POST请求，完整地址是/api/auth/register
    public ApiResponse<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            // 验证密码和确认密码是否一致
            if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
                return ApiResponse.error("400", "密码和确认密码不一致");
            }

            // 创建用户对象
            User user = new User();
            user.setUsername(registerRequestDTO.getUsername());
            user.setPassword(registerRequestDTO.getPassword());
            user.setEmail(registerRequestDTO.getEmail());

            // 调用AuthService处理注册逻辑
            User registeredUser = authService.register(user);

            // 转换为响应DTO（不包含密码等敏感信息）
            UserResponseDTO userResponseDTO = UserResponseDTO.from(registeredUser);

            // 返回成功响应
            return ApiResponse.success(userResponseDTO);

        } catch (IllegalArgumentException e) {
            // 注册失败（用户名或邮箱重复等业务异常）
            return ApiResponse.error("400", e.getMessage());

        } catch (Exception e) {
            // 系统错误
            e.printStackTrace();
            return ApiResponse.error("500", "系统错误，请稍后重试");
        }
    }


    /**
     * 用户登录接口
     *
     * 处理用户登录请求，验证凭据，生成JWT令牌，并返回登录结果
     *
     * @param loginRequestDTO 登录请求数据传输对象
     * @return ApiResponse<LoginResponseDTO> 包含登录结果的响应对象
     * @Operation 用于Swagger文档生成，描述接口功能和参数
     * @PostMapping 处理POST请求，完整地址是/api/auth/login
     * @Valid 用于验证请求体中的数据
     * @RequestBody 用于将请求体转换为LoginRequestDTO对象
     */
    @Operation(summary = "用户登录接口", description = "处理用户登录请求，验证凭据，生成JWT令牌，并返回登录结果")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "系统错误")
    })
    @PostMapping("/login")      // 处理POST请求，完整地址是/api/auth/login
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            // 第一步：验证用户登录凭据
            User loginUser = authService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

            // 第二步：生成JWT令牌
            // JWTService.generateToken()返回带"Bearer "前缀的完整令牌
            String fullToken = jwtService.generateToken(loginUser.getUsername());

            // 提取不带"Bearer "前缀的纯令牌，用于前端存储
            String pureToken = fullToken;
            if (fullToken.startsWith("Bearer ")) {
                pureToken = fullToken.substring(7);
            }

            // 生成唯一的JWT ID
            String jti = UUID.randomUUID().toString();

            // 第三步：构建令牌响应DTO
            LoginResponseDTO.TokenDTO tokenDTO = LoginResponseDTO.TokenDTO.builder()
                    .authorization("Bearer")                                    // 认证类型
                    .jti(jti)                                                  // JWT唯一标识
                    .access_token(pureToken)                                   // 访问令牌（不含Bearer前缀）
                    .refresh_token(null)                                       // 刷新令牌（暂未实现）
                    .expires_in((int)(jwtService.getExpirationTime() / 1000))  // 过期时间（秒）
                    .build();

            // 第四步：构建用户响应DTO（不包含密码等敏感信息）
            UserResponseDTO userResponseDTO = UserResponseDTO.from(loginUser);

            // 第五步：构建完整的登录响应DTO
            LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                    .user(userResponseDTO)
                    .token(tokenDTO)
                    .build();

            // 第六步：返回成功响应
            return ApiResponse.success(loginResponseDTO);

        } catch (IllegalArgumentException e) {
            // 登录失败（用户名密码错误、账户被禁用等业务异常）
            return ApiResponse.error("401", e.getMessage());

        } catch (Exception e) {
            // 系统错误
            e.printStackTrace();
            return ApiResponse.error("500", "系统错误，请稍后重试");
        }
    }

}
