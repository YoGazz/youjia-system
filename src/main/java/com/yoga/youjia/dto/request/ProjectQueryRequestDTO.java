package com.yoga.youjia.dto.request;

import com.yoga.youjia.common.enums.ProjectPriority;
import com.yoga.youjia.common.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 项目查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectQueryRequestDTO {
    
    /**
     * 项目状态
     */
    private ProjectStatus status;
    
    /**
     * 项目优先级
     */
    private ProjectPriority priority;
    
    /**
     * 创建者ID
     */
    private Long createdBy;
    
    /**
     * 关键词（搜索项目编码、名称、描述）
     */
    private String keyword;
    
    /**
     * 标签
     */
    private String tag;
    
    /**
     * 开始时间范围-起始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateFrom;
    
    /**
     * 开始时间范围-结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTo;
    
    /**
     * 结束时间范围-起始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateFrom;
    
    /**
     * 结束时间范围-结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTo;
    
    /**
     * 页码（从0开始）
     */
    @Builder.Default
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    @Builder.Default
    private Integer size = 20;
    
    /**
     * 排序字段
     */
    @Builder.Default
    private String sortBy = "updatedAt";
    
    /**
     * 排序方向
     */
    @Builder.Default
    private String sortDir = "desc";
}