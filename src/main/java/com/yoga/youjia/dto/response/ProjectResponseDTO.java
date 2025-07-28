package com.yoga.youjia.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoga.youjia.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    
    /**
     * 项目ID
     */
    private Long id;
    
    /**
     * 项目编码
     */
    private String code;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目状态代码
     */
    private String status;
    
    /**
     * 项目状态显示名称
     */
    private String statusLabel;
    
    /**
     * 项目优先级代码
     */
    private String priority;
    
    /**
     * 项目优先级显示名称
     */
    private String priorityLabel;
    
    /**
     * 项目优先级颜色
     */
    private String priorityColor;
    
    /**
     * 项目开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    /**
     * 项目预计结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    /**
     * 项目实际结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEndDate;
    
    /**
     * 项目版本号
     */
    private String version;
    
    /**
     * 项目标签列表
     */
    private List<String> tags;
    
    /**
     * 项目设置
     */
    private String settings;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 是否活跃
     */
    private Boolean active;
    
    /**
     * 是否已结束
     */
    private Boolean finished;
    
    /**
     * 是否逾期
     */
    private Boolean overdue;
    
    /**
     * 时间进度百分比
     */
    private Double timeProgress;
    
    /**
     * 项目持续时间（天数）
     */
    private Long durationInDays;
    
    /**
     * 成员数量
     */
    private Integer memberCount;
    
    /**
     * 创建者ID
     */
    private Long createdBy;
    
    /**
     * 更新者ID
     */
    private Long updatedBy;
    
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
     * 将Project实体转换为ProjectResponseDTO
     */
    public static ProjectResponseDTO from(Project project) {
        if (project == null) {
            return null;
        }
        
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .code(project.getCode())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus() != null ? project.getStatus().name() : null)
                .statusLabel(project.getStatusDisplayName())
                .priority(project.getPriority() != null ? project.getPriority().name() : null)
                .priorityLabel(project.getPriorityDisplayName())
                .priorityColor(project.getPriority() != null ? project.getPriority().getColor() : null)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .actualEndDate(project.getActualEndDate())
                .version(project.getVersion())
                .tags(project.getTagList())
                .settings(project.getSettings())
                .enabled(project.getEnabled())
                .active(project.isActive())
                .finished(project.isFinished())
                .overdue(project.isOverdue())
                .timeProgress(project.getTimeProgress())
                .durationInDays(project.getDurationInDays())
                .memberCount(project.getMemberCount())
                .createdBy(project.getCreatedBy())
                .updatedBy(project.getUpdatedBy())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
    
    /**
     * 创建简化版的项目响应DTO（仅包含基本信息）
     */
    public static ProjectResponseDTO simple(Project project) {
        if (project == null) {
            return null;
        }
        
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .code(project.getCode())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus() != null ? project.getStatus().name() : null)
                .statusLabel(project.getStatusDisplayName())
                .priority(project.getPriority() != null ? project.getPriority().name() : null)
                .priorityLabel(project.getPriorityDisplayName())
                .priorityColor(project.getPriority() != null ? project.getPriority().getColor() : null)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .enabled(project.getEnabled())
                .active(project.isActive())
                .finished(project.isFinished())
                .overdue(project.isOverdue())
                .memberCount(project.getMemberCount())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}