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
 * @Data 注解用于生成getter、setter、toString等方法
 * @Builder 注解用于生成Builder模式的构造方法
 * @NoArgsConstructor 和 @AllArgsConstructor 注解用于生成无参和全参构造方法
 * @JsonFormat 注解用于格式化日期时间字段
 * 这个DTO类主要用于在API响应中返回用户信息
 * 例如：在获取用户信息的API中返回用户的ID、用户名、邮箱、状态、角色等信息
 * 注意：DTO（数据传输对象）通常用于在不同层之间传递数据，避免直接暴露实体类
 * 这样可以保护实体类的封装性和安全性，避免敏感信息泄露
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
     * 如果user为null，返回null
     * 这个方法主要用于在Controller层将User实体转换为UserResponseDTO，
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
