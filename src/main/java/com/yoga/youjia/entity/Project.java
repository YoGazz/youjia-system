package com.yoga.youjia.entity;

import com.yoga.youjia.common.enums.ProjectPriority;
import com.yoga.youjia.common.enums.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目实体类
 * 
 * 用于管理测试项目的基本信息、状态和配置
 */
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_project_code", columnList = "code"),
    @Index(name = "idx_project_status", columnList = "status"),
    @Index(name = "idx_project_priority", columnList = "priority"),
    @Index(name = "idx_project_created_by", columnList = "created_by"),
    @Index(name = "idx_project_start_date", columnList = "start_date"),
    @Index(name = "idx_project_end_date", columnList = "end_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 项目编码 - 唯一标识
     */
    @Column(name = "code", unique = true, nullable = false, length = 50)
    @NotBlank(message = "项目编码不能为空")
    @Size(min = 2, max = 50, message = "项目编码长度必须在2-50个字符之间")
    private String code;
    
    /**
     * 项目名称
     */
    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "项目名称不能为空")
    @Size(min = 2, max = 200, message = "项目名称长度必须在2-200个字符之间")
    private String name;
    
    /**
     * 项目描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 2000, message = "项目描述不能超过2000个字符")
    private String description;
    
    /**
     * 项目状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "项目状态不能为空")
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNING;
    
    /**
     * 项目优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @NotNull(message = "项目优先级不能为空")
    @Builder.Default
    private ProjectPriority priority = ProjectPriority.MEDIUM;
    
    /**
     * 项目开始时间
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    /**
     * 项目预计结束时间
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    /**
     * 项目实际结束时间
     */
    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;
    
    /**
     * 项目版本号
     */
    @Column(name = "version", length = 50)
    @Size(max = 50, message = "版本号不能超过50个字符")
    private String version;
    
    /**
     * 项目标签（用逗号分隔）
     */
    @Column(name = "tags", length = 500)
    @Size(max = 500, message = "标签不能超过500个字符")
    private String tags;
    
    /**
     * 项目设置（JSON格式存储）
     */
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    /**
     * 创建者ID
     */
    @Column(name = "created_by", nullable = false)
    @NotNull(message = "创建者ID不能为空")
    private Long createdBy;
    
    /**
     * 更新者ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;
    
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
    
    /**
     * 项目成员列表
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();
    
    // ========== 业务方法 ==========
    
    /**
     * 获取项目状态显示名称
     */
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "未知";
    }
    
    /**
     * 获取项目优先级显示名称
     */
    public String getPriorityDisplayName() {
        return priority != null ? priority.getDisplayName() : "未知";
    }
    
    /**
     * 检查项目是否为活跃状态
     */
    public boolean isActive() {
        return enabled && status != null && status.isActive();
    }
    
    /**
     * 检查项目是否已结束
     */
    public boolean isFinished() {
        return status != null && status.isFinished();
    }
    
    /**
     * 检查项目是否逾期
     */
    public boolean isOverdue() {
        if (endDate == null || isFinished()) {
            return false;
        }
        return LocalDateTime.now().isAfter(endDate);
    }
    
    /**
     * 获取项目进度百分比（基于时间）
     */
    public Double getTimeProgress() {
        if (startDate == null || endDate == null) {
            return 0.0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime actualStart = startDate;
        LocalDateTime actualEnd = actualEndDate != null ? actualEndDate : endDate;
        
        if (now.isBefore(actualStart)) {
            return 0.0;
        }
        
        if (now.isAfter(actualEnd)) {
            return 100.0;
        }
        
        long totalDuration = java.time.Duration.between(actualStart, actualEnd).toDays();
        long elapsedDuration = java.time.Duration.between(actualStart, now).toDays();
        
        if (totalDuration <= 0) {
            return 100.0;
        }
        
        return Math.min(100.0, (double) elapsedDuration / totalDuration * 100);
    }
    
    /**
     * 获取项目持续时间（天数）
     */
    public Long getDurationInDays() {
        if (startDate == null || endDate == null) {
            return null;
        }
        return java.time.Duration.between(startDate, endDate).toDays();
    }
    
    /**
     * 转换项目状态
     */
    public boolean changeStatus(ProjectStatus newStatus, Long operatorId) {
        if (newStatus == null || newStatus == this.status) {
            return false;
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("项目状态不能从 %s 转换为 %s", 
                    this.status.getDisplayName(), newStatus.getDisplayName()));
        }
        
        this.status = newStatus;
        this.updatedBy = operatorId;
        
        // 如果项目完成，设置实际结束时间
        if (newStatus == ProjectStatus.COMPLETED && this.actualEndDate == null) {
            this.actualEndDate = LocalDateTime.now();
        }
        
        return true;
    }
    
    /**
     * 添加项目成员
     */
    public void addMember(ProjectMember member) {
        if (member != null && !members.contains(member)) {
            members.add(member);
            member.setProject(this);
        }
    }
    
    /**
     * 移除项目成员
     */
    public void removeMember(ProjectMember member) {
        if (member != null && members.contains(member)) {
            members.remove(member);
            member.setProject(null);
        }
    }
    
    /**
     * 获取项目成员数量
     */
    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }
    
    /**
     * 检查用户是否为项目成员
     */
    public boolean hasMember(Long userId) {
        if (userId == null || members == null) {
            return false;
        }
        return members.stream().anyMatch(member -> userId.equals(member.getUserId()));
    }
    
    /**
     * 获取项目标签列表
     */
    public List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> tagList = new ArrayList<>();
        for (String tag : tags.split(",")) {
            String trimmedTag = tag.trim();
            if (!trimmedTag.isEmpty()) {
                tagList.add(trimmedTag);
            }
        }
        return tagList;
    }
    
    /**
     * 设置项目标签列表
     */
    public void setTagList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = String.join(",", tagList);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id != null && id.equals(project.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("Project{id=%d, code='%s', name='%s', status=%s}", 
                           id, code, name, status);
    }
}