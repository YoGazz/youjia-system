package com.yoga.youjia.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoga.youjia.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目成员响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponseDTO {
    
    /**
     * 成员记录ID
     */
    private Long id;
    
    /**
     * 项目ID
     */
    private Long projectId;
    
    /**
     * 项目编码
     */
    private String projectCode;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 角色代码
     */
    private String role;
    
    /**
     * 角色显示名称
     */
    private String roleLabel;
    
    /**
     * 是否活跃
     */
    private Boolean active;
    
    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinedAt;
    
    /**
     * 离开时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leftAt;
    
    /**
     * 备注信息
     */
    private String remarks;
    
    /**
     * 添加者ID
     */
    private Long addedBy;
    
    /**
     * 在项目中的天数
     */
    private Long daysInProject;
    
    /**
     * 权限信息
     */
    private ProjectMemberPermissionDTO permissions;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 将ProjectMember实体转换为ProjectMemberResponseDTO
     */
    public static ProjectMemberResponseDTO from(ProjectMember member) {
        if (member == null) {
            return null;
        }
        
        return ProjectMemberResponseDTO.builder()
                .id(member.getId())
                .projectId(member.getProjectId())
                .projectCode(member.getProjectCode())
                .projectName(member.getProjectName())
                .userId(member.getUserId())
                .role(member.getRole() != null ? member.getRole().name() : null)
                .roleLabel(member.getRoleDisplayName())
                .active(member.isActive())
                .joinedAt(member.getJoinedAt())
                .leftAt(member.getLeftAt())
                .remarks(member.getRemarks())
                .addedBy(member.getAddedBy())
                .daysInProject(member.getDaysInProject())
                .permissions(ProjectMemberPermissionDTO.from(member))
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
    
    /**
     * 创建简化版的项目成员响应DTO
     */
    public static ProjectMemberResponseDTO simple(ProjectMember member) {
        if (member == null) {
            return null;
        }
        
        return ProjectMemberResponseDTO.builder()
                .id(member.getId())
                .projectId(member.getProjectId())
                .userId(member.getUserId())
                .role(member.getRole() != null ? member.getRole().name() : null)
                .roleLabel(member.getRoleDisplayName())
                .active(member.isActive())
                .joinedAt(member.getJoinedAt())
                .daysInProject(member.getDaysInProject())
                .build();
    }
}

/**
 * 项目成员权限DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ProjectMemberPermissionDTO {
    
    /**
     * 是否可编辑
     */
    private Boolean canEdit;
    
    /**
     * 是否可删除
     */
    private Boolean canDelete;
    
    /**
     * 是否可管理成员
     */
    private Boolean canManageMembers;
    
    /**
     * 是否可创建测试用例
     */
    private Boolean canCreateTestCases;
    
    /**
     * 是否可执行测试
     */
    private Boolean canExecuteTests;
    
    /**
     * 是否可管理缺陷
     */
    private Boolean canManageBugs;
    
    /**
     * 是否为管理员
     */
    private Boolean isManager;
    
    /**
     * 从ProjectMember创建权限DTO
     */
    public static ProjectMemberPermissionDTO from(ProjectMember member) {
        if (member == null) {
            return null;
        }
        
        return ProjectMemberPermissionDTO.builder()
                .canEdit(member.canEdit())
                .canDelete(member.canDelete())
                .canManageMembers(member.canManageMembers())
                .canCreateTestCases(member.canCreateTestCases())
                .canExecuteTests(member.canExecuteTests())
                .canManageBugs(member.canManageBugs())
                .isManager(member.isManager())
                .build();
    }
}