package com.yoga.youjia.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoga.youjia.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应数据传输对象
 *
 * 用于封装用户相关的响应数据  不包含敏感信息如密码等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String status; // 用户状态（如：激活、未激活、禁用等）
    private String role; // 用户角色（如：管理员、普通用户等）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 创建时间

    /**
     * 将User实体转换为UserResponseDTO
     * @param user 用户实体对象
     * @return 用户响应DTO
     */
    public static UserResponseDTO from(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
