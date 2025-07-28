package com.yoga.youjia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建测试模块请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "创建测试模块请求")
public class CreateTestModuleRequestDTO {
    
    @Schema(description = "模块名称", example = "用户管理模块")
    @NotBlank(message = "模块名称不能为空")
    @Size(max = 200, message = "模块名称长度不能超过200字符")
    private String name;
    
    @Schema(description = "模块描述", example = "用户注册、登录、个人信息管理等功能的测试")
    @Size(max = 2000, message = "模块描述长度不能超过2000字符")
    private String description;
    
    @Schema(description = "父模块ID（为空表示根模块）", example = "1")
    private Long parentId;
    
    @Schema(description = "排序序号", example = "1")
    @PositiveOrZero(message = "排序序号不能为负数")
    private Integer sortOrder = 0;
}