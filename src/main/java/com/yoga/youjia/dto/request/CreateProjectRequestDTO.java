package com.yoga.youjia.dto.request;

import com.yoga.youjia.common.enums.ProjectPriority;
import com.yoga.youjia.common.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建项目请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequestDTO {
    
    /**
     * 项目编码
     */
    @NotBlank(message = "项目编码不能为空")
    @Size(min = 2, max = 50, message = "项目编码长度必须在2-50个字符之间")
    private String code;
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(min = 2, max = 200, message = "项目名称长度必须在2-200个字符之间")
    private String name;
    
    /**
     * 项目描述
     */
    @Size(max = 2000, message = "项目描述不能超过2000个字符")
    private String description;
    
    /**
     * 项目优先级
     */
    @NotNull(message = "项目优先级不能为空")
    private ProjectPriority priority;
    
    /**
     * 项目开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    /**
     * 项目预计结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    /**
     * 项目版本号
     */
    @Size(max = 50, message = "版本号不能超过50个字符")
    private String version;
    
    /**
     * 项目标签列表
     */
    private List<String> tags;
    
    /**
     * 项目设置（JSON格式）
     */
    private String settings;
}