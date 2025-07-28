package com.yoga.youjia.dto.request;

import com.yoga.youjia.common.enums.TestCasePriority;
import com.yoga.youjia.common.enums.TestCaseStatus;
import com.yoga.youjia.common.enums.TestCaseType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试用例查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "测试用例查询条件")
public class TestCaseQueryRequestDTO {
    
    @Schema(description = "用例标题（模糊查询）", example = "登录")
    private String title;
    
    @Schema(description = "所属模块ID", example = "1")
    private Long moduleId;
    
    @Schema(description = "测试用例类型", example = "FUNCTIONAL")
    private TestCaseType type;
    
    @Schema(description = "优先级", example = "HIGH")
    private TestCasePriority priority;
    
    @Schema(description = "用例状态", example = "APPROVED")
    private TestCaseStatus status;
    
    @Schema(description = "是否自动化", example = "true")
    private Boolean automated;
    
    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;
    
    @Schema(description = "标签（模糊查询）", example = "登录")
    private String tag;
    
    @Schema(description = "关键字（全文搜索）", example = "用户登录")
    private String keyword;
    
    @Schema(description = "页码（从0开始）", example = "0")
    private Integer page = 0;
    
    @Schema(description = "每页大小", example = "20")
    private Integer size = 20;
    
    @Schema(description = "排序字段", example = "createdAt")
    private String sortBy = "sortOrder";
    
    @Schema(description = "排序方向", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "ASC";
}