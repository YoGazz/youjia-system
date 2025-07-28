package com.yoga.youjia.dto.response;

import com.yoga.youjia.common.enums.TestCasePriority;
import com.yoga.youjia.common.enums.TestCaseStatus;
import com.yoga.youjia.common.enums.TestCaseType;
import com.yoga.youjia.entity.TestCase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "测试用例信息")
public class TestCaseResponseDTO {
    
    @Schema(description = "用例ID", example = "1")
    private Long id;
    
    @Schema(description = "用例编号", example = "TC_YJ_LOGIN_001")
    private String caseId;
    
    @Schema(description = "用例标题", example = "用户登录功能测试")
    private String title;
    
    @Schema(description = "用例描述", example = "验证用户使用正确的用户名和密码能够成功登录系统")
    private String description;
    
    @Schema(description = "前置条件", example = "1. 用户已注册\\n2. 系统正常运行")
    private String preconditions;
    
    @Schema(description = "测试数据", example = "用户名: testuser\\n密码: password123")
    private String testData;
    
    @Schema(description = "预期结果", example = "用户成功登录，跳转到首页")
    private String expectedResult;
    
    @Schema(description = "后置条件", example = "清理测试数据")
    private String postconditions;
    
    @Schema(description = "测试用例类型", example = "FUNCTIONAL")
    private TestCaseType type;
    
    @Schema(description = "优先级", example = "MEDIUM")
    private TestCasePriority priority;
    
    @Schema(description = "用例状态", example = "APPROVED")
    private TestCaseStatus status;
    
    @Schema(description = "是否自动化", example = "false")
    private Boolean automated;
    
    @Schema(description = "自动化脚本路径", example = "/scripts/login_test.py")
    private String automationScript;
    
    @Schema(description = "标签列表", example = "[\"登录\", \"用户管理\", \"冒烟测试\"]")
    private List<String> tags;
    
    @Schema(description = "所属项目ID", example = "1")
    private Long projectId;
    
    @Schema(description = "所属模块ID", example = "1")
    private Long moduleId;
    
    @Schema(description = "所属模块名称", example = "用户管理")
    private String moduleName;
    
    @Schema(description = "关联需求ID", example = "1")
    private Long requirementId;
    
    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "预估执行时间（分钟）", example = "10")
    private Integer estimatedTime;
    
    @Schema(description = "用例版本号", example = "1")
    private Integer version;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;
    
    @Schema(description = "创建人姓名", example = "张三")
    private String createdByName;
    
    @Schema(description = "更新人ID", example = "1")
    private Long updatedBy;
    
    @Schema(description = "更新人姓名", example = "李四")
    private String updatedByName;
    
    @Schema(description = "审核人ID", example = "1")
    private Long reviewedBy;
    
    @Schema(description = "审核人姓名", example = "王五")
    private String reviewedByName;
    
    @Schema(description = "审核时间", example = "2024-01-15T10:30:00")
    private LocalDateTime reviewedAt;
    
    @Schema(description = "审核意见", example = "用例设计合理，通过审核")
    private String reviewComment;
    
    @Schema(description = "创建时间", example = "2024-01-15T09:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "测试步骤列表")
    private List<TestStepResponseDTO> testSteps;
    
    @Schema(description = "步骤总数", example = "5")
    private Integer stepCount;
    
    @Schema(description = "是否可以执行", example = "true")
    private Boolean canExecute;
    
    @Schema(description = "是否可以编辑", example = "false")
    private Boolean canEdit;
    
    @Schema(description = "是否可以审核", example = "false")
    private Boolean canReview;
    
    /**
     * 从实体类转换为响应DTO
     */
    public static TestCaseResponseDTO from(TestCase testCase) {
        if (testCase == null) {
            return null;
        }
        
        TestCaseResponseDTOBuilder builder = TestCaseResponseDTO.builder()
                .id(testCase.getId())
                .caseId(testCase.getCaseId())
                .title(testCase.getTitle())
                .description(testCase.getDescription())
                .preconditions(testCase.getPreconditions())
                .testData(testCase.getTestData())
                .expectedResult(testCase.getExpectedResult())
                .postconditions(testCase.getPostconditions())
                .type(testCase.getType())
                .priority(testCase.getPriority())
                .status(testCase.getStatus())
                .automated(testCase.getAutomated())
                .automationScript(testCase.getAutomationScript())
                .tags(testCase.getTagList())
                .projectId(testCase.getProjectId())
                .moduleId(testCase.getModuleId())
                .requirementId(testCase.getRequirementId())
                .sortOrder(testCase.getSortOrder())
                .estimatedTime(testCase.getEstimatedTime())
                .version(testCase.getVersion())
                .enabled(testCase.getEnabled())
                .createdBy(testCase.getCreatedBy())
                .updatedBy(testCase.getUpdatedBy())
                .reviewedBy(testCase.getReviewedBy())
                .reviewedAt(testCase.getReviewedAt())
                .reviewComment(testCase.getReviewComment())
                .createdAt(testCase.getCreatedAt())
                .updatedAt(testCase.getUpdatedAt())
                .stepCount(testCase.getStepCount())
                .canExecute(testCase.canExecute())
                .canEdit(testCase.canEdit())
                .canReview(testCase.canReview());
        
        // 设置模块名称
        if (testCase.getTestModule() != null) {
            builder.moduleName(testCase.getTestModule().getName());
        }
        
        // 设置创建人姓名
        if (testCase.getCreator() != null) {
            builder.createdByName(testCase.getCreator().getRealName());
        }
        
        // 设置更新人姓名
        if (testCase.getUpdater() != null) {
            builder.updatedByName(testCase.getUpdater().getRealName());
        }
        
        // 设置审核人姓名
        if (testCase.getReviewer() != null) {
            builder.reviewedByName(testCase.getReviewer().getRealName());
        }
        
        // 转换测试步骤
        if (testCase.getTestSteps() != null) {
            List<TestStepResponseDTO> stepDTOs = testCase.getTestSteps().stream()
                    .map(TestStepResponseDTO::from)
                    .collect(Collectors.toList());
            builder.testSteps(stepDTOs);
        }
        
        return builder.build();
    }
    
    /**
     * 创建简化版本（不包含步骤详情）
     */
    public static TestCaseResponseDTO fromSimple(TestCase testCase) {
        TestCaseResponseDTO dto = from(testCase);
        if (dto != null) {
            dto.setTestSteps(null); // 移除步骤详情以减少数据量
        }
        return dto;
    }
}