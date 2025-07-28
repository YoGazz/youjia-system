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
 * 用于封装用户相关的响应数据，不包含敏感信息如密码等
 * 支持新的用户角色权限系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 用户状态代码
     */
    private String status;
    
    /**
     * 用户状态显示名称
     */
    private String statusLabel;
    
    /**
     * 用户角色代码
     */
    private String role;
    
    /**
     * 用户角色显示名称
     */
    private String roleLabel;
    
    /**
     * 用户简介
     */
    private String description;
    
    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    /**
     * 账户创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 账户更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 是否可以登录
     */
    private Boolean canLogin;
    
    /**
     * 是否为管理员
     */
    private Boolean isAdmin;

    /**
     * 将User实体转换为UserResponseDTO
     * 
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
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .statusLabel(user.getStatusDisplayName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .roleLabel(user.getRoleDisplayName())
                .description(user.getDescription())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .canLogin(user.canLogin())
                .isAdmin(user.isAdmin())
                .build();
    }
    
    /**
     * 创建简化版的用户响应DTO（仅包含基本信息）
     */
    public static UserResponseDTO simple(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .statusLabel(user.getStatusDisplayName())
                .roleLabel(user.getRoleDisplayName())
                .build();
    }
    
    /**
     * 创建公开版的用户响应DTO（隐藏敏感信息）
     */
    public static UserResponseDTO publicInfo(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .roleLabel(user.getRoleDisplayName())
                .build();
    }
    
    /**
     * 获取用户显示名称（优先显示真实姓名）
     */
    public String getDisplayName() {
        return realName != null && !realName.trim().isEmpty() ? realName : username;
    }
}
