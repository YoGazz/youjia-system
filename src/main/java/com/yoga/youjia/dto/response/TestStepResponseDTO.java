package com.yoga.youjia.dto.response;

import com.yoga.youjia.entity.TestStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试步骤响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "测试步骤信息")
public class TestStepResponseDTO {
    
    @Schema(description = "步骤ID", example = "1")
    private Long id;
    
    @Schema(description = "所属测试用例ID", example = "1")
    private Long testCaseId;
    
    @Schema(description = "步骤序号", example = "1")
    private Integer stepOrder;
    
    @Schema(description = "步骤描述", example = "打开登录页面")
    private String stepDescription;
    
    @Schema(description = "测试数据", example = "URL: http://localhost:8080/login")
    private String testData;
    
    @Schema(description = "预期结果", example = "登录页面正常显示")
    private String expectedResult;
    
    @Schema(description = "备注", example = "注意检查页面样式")
    private String remark;
    
    @Schema(description = "是否为关键步骤", example = "true")
    private Boolean isKeyStep;
    
    @Schema(description = "是否自动化", example = "false")
    private Boolean automated;
    
    @Schema(description = "自动化脚本代码")
    private String automationCode;
    
    @Schema(description = "预估执行时间（秒）", example = "30")
    private Integer estimatedTime;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    @Schema(description = "创建时间", example = "2024-01-15T09:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "步骤描述（简短版本）", example = "打开登录页面")
    private String shortDescription;
    
    @Schema(description = "预期结果（简短版本）", example = "登录页面正常显示")
    private String shortExpectedResult;
    
    @Schema(description = "是否完整", example = "true")
    private Boolean complete;
    
    @Schema(description = "是否可以自动化", example = "false")
    private Boolean canAutomate;
    
    /**
     * 从实体类转换为响应DTO
     */
    public static TestStepResponseDTO from(TestStep testStep) {
        if (testStep == null) {
            return null;
        }
        
        return TestStepResponseDTO.builder()
                .id(testStep.getId())
                .testCaseId(testStep.getTestCaseId())
                .stepOrder(testStep.getStepOrder())
                .stepDescription(testStep.getStepDescription())
                .testData(testStep.getTestData())
                .expectedResult(testStep.getExpectedResult())
                .remark(testStep.getRemark())
                .isKeyStep(testStep.getIsKeyStep())
                .automated(testStep.getAutomated())
                .automationCode(testStep.getAutomationCode())
                .estimatedTime(testStep.getEstimatedTime())
                .enabled(testStep.getEnabled())
                .createdAt(testStep.getCreatedAt())
                .updatedAt(testStep.getUpdatedAt())
                .shortDescription(testStep.getShortDescription())
                .shortExpectedResult(testStep.getShortExpectedResult())
                .complete(testStep.isComplete())
                .canAutomate(testStep.canAutomate())
                .build();
    }
}