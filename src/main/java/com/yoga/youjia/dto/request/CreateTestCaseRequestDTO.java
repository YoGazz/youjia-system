package com.yoga.youjia.dto.request;

import com.yoga.youjia.common.enums.TestCasePriority;
import com.yoga.youjia.common.enums.TestCaseType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建测试用例请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "创建测试用例请求")
public class CreateTestCaseRequestDTO {
    
    @Schema(description = "用例标题", example = "用户登录功能测试")
    @NotBlank(message = "用例标题不能为空")
    @Size(max = 500, message = "用例标题长度不能超过500字符")
    private String title;
    
    @Schema(description = "用例描述", example = "验证用户使用正确的用户名和密码能够成功登录系统")
    @Size(max = 2000, message = "用例描述长度不能超过2000字符")
    private String description;
    
    @Schema(description = "前置条件", example = "1. 用户已注册\n2. 系统正常运行")
    @Size(max = 2000, message = "前置条件长度不能超过2000字符")
    private String preconditions;
    
    @Schema(description = "测试数据", example = "用户名: testuser\n密码: password123")
    @Size(max = 2000, message = "测试数据长度不能超过2000字符")
    private String testData;
    
    @Schema(description = "预期结果", example = "用户成功登录，跳转到首页")
    @Size(max = 2000, message = "预期结果长度不能超过2000字符")
    private String expectedResult;
    
    @Schema(description = "后置条件", example = "清理测试数据")
    @Size(max = 2000, message = "后置条件长度不能超过2000字符")
    private String postconditions;
    
    @Schema(description = "测试用例类型", example = "FUNCTIONAL")
    private TestCaseType type = TestCaseType.FUNCTIONAL;
    
    @Schema(description = "优先级", example = "MEDIUM")
    private TestCasePriority priority = TestCasePriority.MEDIUM;
    
    @Schema(description = "是否自动化", example = "false")
    private Boolean automated = false;
    
    @Schema(description = "自动化脚本路径", example = "/scripts/login_test.py")
    @Size(max = 500, message = "自动化脚本路径长度不能超过500字符")
    private String automationScript;
    
    @Schema(description = "标签列表", example = "[\"登录\", \"用户管理\", \"冒烟测试\"]")
    private List<String> tags;
    
    @Schema(description = "所属模块ID", example = "1")
    @NotNull(message = "所属模块ID不能为空")
    @Positive(message = "模块ID必须为正数")
    private Long moduleId;
    
    @Schema(description = "关联需求ID", example = "1")
    private Long requirementId;
    
    @Schema(description = "预估执行时间（分钟）", example = "10")
    @Min(value = 1, message = "预估执行时间不能小于1分钟")
    @Max(value = 480, message = "预估执行时间不能超过8小时")
    private Integer estimatedTime = 5;
    
    @Schema(description = "测试步骤列表")
    private List<CreateTestStepRequestDTO> testSteps;
    
    /**
     * 创建测试步骤请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "创建测试步骤请求")
    public static class CreateTestStepRequestDTO {
        
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
}