package com.yoga.youjia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建测试步骤请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "创建测试步骤请求")
public class CreateTestStepRequestDTO {
    
    @Schema(description = "步骤序号", example = "1")
    @NotNull(message = "步骤序号不能为空")
    @Positive(message = "步骤序号必须为正数")
    private Integer stepOrder;
    
    @Schema(description = "步骤描述", example = "打开登录页面")
    @NotBlank(message = "步骤描述不能为空")
    @Size(max = 2000, message = "步骤描述长度不能超过2000字符")
    private String stepDescription;
    
    @Schema(description = "测试数据", example = "URL: http://localhost:8080/login")
    @Size(max = 2000, message = "测试数据长度不能超过2000字符")
    private String testData;
    
    @Schema(description = "预期结果", example = "登录页面正常显示")
    @NotBlank(message = "预期结果不能为空")
    @Size(max = 2000, message = "预期结果长度不能超过2000字符")
    private String expectedResult;
    
    @Schema(description = "备注", example = "注意检查页面样式")
    @Size(max = 1000, message = "备注长度不能超过1000字符")
    private String remark;
    
    @Schema(description = "是否为关键步骤", example = "true")
    private Boolean isKeyStep = false;
    
    @Schema(description = "是否自动化", example = "false")
    private Boolean automated = false;
    
    @Schema(description = "自动化脚本代码")
    @Size(max = 5000, message = "自动化脚本代码长度不能超过5000字符")
    private String automationCode;
    
    @Schema(description = "预估执行时间（秒）", example = "30")
    @Min(value = 1, message = "预估执行时间不能小于1秒")
    @Max(value = 3600, message = "预估执行时间不能超过1小时")
    private Integer estimatedTime = 30;
}