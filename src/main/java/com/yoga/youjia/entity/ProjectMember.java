package com.yoga.youjia.entity;

import com.yoga.youjia.common.enums.ProjectMemberRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 项目成员实体类
 * 
 * 管理项目成员的角色和权限关系
 */
@Entity
@Table(name = "project_members", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_project_user", columnNames = {"project_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_project_member_project", columnList = "project_id"),
        @Index(name = "idx_project_member_user", columnList = "user_id"),
        @Index(name = "idx_project_member_role", columnList = "role"),
        @Index(name = "idx_project_member_joined", columnList = "joined_at")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的项目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull(message = "项目ID不能为空")
    private Project project;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 在项目中的角色
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @NotNull(message = "项目角色不能为空")
    private ProjectMemberRole role;
    
    /**
     * 是否为活跃成员
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    /**
     * 加入项目时间
     */
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    /**
     * 离开项目时间
     */
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    /**
     * 备注信息
     */
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    /**
     * 添加成员的操作者ID
     */
    @Column(name = "added_by", nullable = false)
    @NotNull(message = "添加者ID不能为空")
    private Long addedBy;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // ========== 业务方法 ==========
    
    /**
     * 获取角色显示名称
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "未知";
    }
    
    /**
     * 检查是否为管理角色
     */
    public boolean isManager() {
        return role != null && role.isManager();
    }
    
    /**
     * 检查是否有编辑权限
     */
    public boolean canEdit() {
        return active && role != null && role.canEdit();
    }
    
    /**
     * 检查是否有删除权限
     */
    public boolean canDelete() {
        return active && role != null && role.canDelete();
    }
    
    /**
     * 检查是否有管理成员权限
     */
    public boolean canManageMembers() {
        return active && role != null && role.canManageMembers();
    }
    
    /**
     * 检查是否有创建测试用例权限
     */
    public boolean canCreateTestCases() {
        return active && role != null && role.canCreateTestCases();
    }
    
    /**
     * 检查是否有执行测试权限
     */
    public boolean canExecuteTests() {
        return active && role != null && role.canExecuteTests();
    }
    
    /**
     * 检查是否有缺陷管理权限
     */
    public boolean canManageBugs() {
        return active && role != null && role.canManageBugs();
    }
    
    /**
     * 检查权限是否高于另一个成员
     */
    public boolean hasHigherRoleThan(ProjectMember other) {
        if (other == null || this.role == null || other.role == null) {
            return false;
        }
        return this.role.isHigherThan(other.role);
    }
    
    /**
     * 离开项目
     */
    public void leaveProject() {
        this.active = false;
        this.leftAt = LocalDateTime.now();
    }
    
    /**
     * 重新加入项目
     */
    public void rejoinProject() {
        this.active = true;
        this.leftAt = null;
    }
    
    /**
     * 更改角色
     */
    public void changeRole(ProjectMemberRole newRole) {
        if (newRole != null && newRole != this.role) {
            this.role = newRole;
        }
    }
    
    /**
     * 检查成员是否活跃
     */
    public boolean isActive() {
        return active != null && active && leftAt == null;
    }
    
    /**
     * 获取在项目中的天数
     */
    public Long getDaysInProject() {
        LocalDateTime endTime = leftAt != null ? leftAt : LocalDateTime.now();
        if (joinedAt == null) {
            return 0L;
        }
        return java.time.Duration.between(joinedAt, endTime).toDays();
    }
    
    /**
     * 获取项目ID（避免懒加载问题）
     */
    public Long getProjectId() {
        return project != null ? project.getId() : null;
    }
    
    /**
     * 获取项目编码（避免懒加载问题）
     */
    public String getProjectCode() {
        return project != null ? project.getCode() : null;
    }
    
    /**
     * 获取项目名称（避免懒加载问题）
     */
    public String getProjectName() {
        return project != null ? project.getName() : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMember that = (ProjectMember) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("ProjectMember{id=%d, projectId=%d, userId=%d, role=%s, active=%s}", 
                           id, getProjectId(), userId, role, active);
    }
}