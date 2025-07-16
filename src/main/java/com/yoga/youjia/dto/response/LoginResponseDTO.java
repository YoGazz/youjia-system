package com.yoga.youjia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private UserResponseDTO user;  // 用户信息
    private TokenDTO token;        // 令牌信息

    /**
     * 令牌数据传输对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenDTO {
        private String authorization;  // JWT令牌，格式为"Bearer xxxxxx"
        private String jti;            // JWT ID
        private String access_token;   // 访问令牌
        private String refresh_token;  // 刷新令牌
        private Integer expires_in;    // 过期时间(秒)
    }
}
