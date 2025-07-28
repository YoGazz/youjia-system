package com.yoga.youjia.entity;

import com.yoga.youjia.common.enums.UserRole;
import com.yoga.youjia.common.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 映射数据库表：users
 * 支持完整的用户角色权限系统和状态管理
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_role", columnList = "role")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * 用户主键ID，自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名，唯一标识，长度3-20字符
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20之间")
    @Column(unique = true, nullable = false, length = 20)
    private String username;

    /**
     * 用户密码，BCrypt加密存储
     */
    @NotBlank(message = "密码不能为空")
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * 用户真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Column(length = 50)
    private String realName;

    /**
     * 用户邮箱，唯一标识
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 用户手机号
     */
    @Size(max = 15, message = "手机号长度不能超过15位")
    @Column(length = 15)
    private String phone;

    /**
     * 用户头像URL或文件路径
     */
    @Column(length = 500)
    private String avatar;

    /**
     * 用户角色，使用新的角色系统
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    /**
     * 用户状态，使用新的状态系统
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDING;

    /**
     * 用户简介或备注
     */
    @Column(length = 500)
    private String description;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Column(length = 50)
    private String lastLoginIp;

    /**
     * 密码错误次数（用于账户锁定）
     */
    @Column(columnDefinition = "int default 0")
    private Integer passwordErrorCount = 0;

    /**
     * 账户锁定到期时间
     */
    private LocalDateTime lockExpireTime;

    /**
     * 创建者ID（由哪个管理员创建）
     */
    private Long createdBy;

    /**
     * 最后修改者ID
     */
    private Long updatedBy;

    /**
     * 账户创建时间，不可修改
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * 账户最后修改时间，自动更新
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ========== 业务方法 ==========
    
    /**
     * 检查用户是否可以登录
     */
    public boolean canLogin() {
        return status != null && status.canLogin();
    }
    
    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    /**
     * 检查用户是否为项目管理员
     */
    public boolean isProjectManager() {
        return role != null && role.isProjectManager();
    }
    
    /**
     * 检查用户是否为测试管理员
     */
    public boolean isTestManager() {
        return role != null && role.isTestManager();
    }
    
    /**
     * 检查用户是否有权限访问目标角色的资源
     */
    public boolean hasPermission(UserRole targetRole) {
        return role != null && role.hasPermission(targetRole);
    }
    
    /**
     * 检查账户是否被锁定
     */
    public boolean isLocked() {
        if (status == UserStatus.LOCKED) {
            return true;
        }
        
        // 检查是否超过锁定时间
        if (lockExpireTime != null && LocalDateTime.now().isBefore(lockExpireTime)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 增加密码错误次数
     */
    public void incrementPasswordErrorCount() {
        this.passwordErrorCount = (this.passwordErrorCount == null ? 0 : this.passwordErrorCount) + 1;
        
        // 如果密码错误次数超过5次，锁定账户30分钟
        if (this.passwordErrorCount >= 5) {
            this.status = UserStatus.LOCKED;
            this.lockExpireTime = LocalDateTime.now().plusMinutes(30);
        }
    }
    
    /**
     * 重置密码错误次数
     */
    public void resetPasswordErrorCount() {
        this.passwordErrorCount = 0;
        this.lockExpireTime = null;
    }
    
    /**
     * 记录登录信息
     */
    public void recordLoginInfo(String ip) {
        this.lastLoginTime = LocalDateTime.now();
        this.lastLoginIp = ip;
        resetPasswordErrorCount(); // 登录成功后重置错误次数
    }
    
    /**
     * 激活账户
     */
    public void activate() {
        if (status != null && status.canActivate()) {
            this.status = UserStatus.ACTIVE;
            resetPasswordErrorCount();
        }
    }
    
    /**
     * 停用账户
     */
    public void deactivate() {
        if (status != null && status.canDeactivate()) {
            this.status = UserStatus.INACTIVE;
        }
    }
    
    /**
     * 锁定账户
     */
    public void lock() {
        if (status != null && status.canLock()) {
            this.status = UserStatus.LOCKED;
        }
    }
    
    /**
     * 解锁账户
     */
    public void unlock() {
        if (status != null && status.canUnlock()) {
            this.status = UserStatus.ACTIVE;
            resetPasswordErrorCount();
        }
    }
    
    /**
     * 获取用户显示名称（优先显示真实姓名）
     */
    public String getDisplayName() {
        return realName != null && !realName.trim().isEmpty() ? realName : username;
    }
    
    /**
     * 获取角色显示名称
     */
    public String getRoleDisplayName() {
        return role != null ? role.getName() : "";
    }
    
    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        return status != null ? status.getName() : "";
    }
}


