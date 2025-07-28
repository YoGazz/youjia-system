package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.exception.BusinessException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 认证控制器
 *
 * 负责用户认证相关的HTTP请求处理，包括用户注册和登录
 * 
 * 采用RESTful设计规范：
 * - POST /api/auth/register - 用户注册
 * - POST /api/auth/login - 用户登录
 */
@Tag(name = "用户认证", description = "用户注册、登录等认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JWTService jwtService;

    /**
     * 用户注册接口
     *
     * @param registerRequestDTO 注册请求数据
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "处理用户注册请求，创建新用户账户")
    @PostMapping("/register")
    public ApiResponse<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        logger.info("用户注册请求: username={}", registerRequestDTO.getUsername());
        
        // 验证密码和确认密码是否一致
        if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(registerRequestDTO.getPassword());
        user.setEmail(registerRequestDTO.getEmail());

        // 调用AuthService处理注册逻辑
        User registeredUser = authService.register(user);

        // 转换为响应DTO
        UserResponseDTO userResponseDTO = UserResponseDTO.from(registeredUser);
        
        logger.info("用户注册成功: userId={}, username={}", registeredUser.getId(), registeredUser.getUsername());
        return ApiResponse.success(userResponseDTO, "注册成功");
    }

    /**
     * 用户登录接口
     *
     * @param loginRequestDTO 登录请求数据
     * @return 登录结果（包含JWT令牌）
     */
    @Operation(summary = "用户登录", description = "验证用户凭据并生成访问令牌")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("用户登录请求: username={}", loginRequestDTO.getUsername());
        
        // 验证用户登录凭据
        User loginUser = authService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

        // 生成JWT令牌
        String fullToken = jwtService.generateToken(loginUser.getUsername());
        
        // 提取不带"Bearer "前缀的纯令牌
        String pureToken = fullToken;
        if (fullToken.startsWith("Bearer ")) {
            pureToken = fullToken.substring(7);
        }

        // 生成唯一的JWT ID
        String jti = UUID.randomUUID().toString();

        // 构建令牌响应DTO
        LoginResponseDTO.TokenDTO tokenDTO = LoginResponseDTO.TokenDTO.builder()
                .authorization("Bearer")
                .jti(jti)
                .access_token(pureToken)
                .refresh_token(null) // 暂未实现刷新令牌
                .expires_in((int)(jwtService.getExpirationTime() / 1000))
                .build();

        // 构建用户响应DTO
        UserResponseDTO userResponseDTO = UserResponseDTO.from(loginUser);

        // 构建完整的登录响应DTO
        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .user(userResponseDTO)
                .token(tokenDTO)
                .build();

        logger.info("用户登录成功: userId={}, username={}", loginUser.getId(), loginUser.getUsername());
        return ApiResponse.success(loginResponseDTO, "登录成功");
    }
}
