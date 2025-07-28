package com.yoga.youjia.dto.response;

import com.yoga.youjia.entity.TestModule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试模块响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "测试模块信息")
public class TestModuleResponseDTO {
    
    @Schema(description = "模块ID", example = "1")
    private Long id;
    
    @Schema(description = "模块名称", example = "用户管理模块")
    private String name;
    
    @Schema(description = "模块描述", example = "用户注册、登录、个人信息管理等功能的测试")
    private String description;
    
    @Schema(description = "所属项目ID", example = "1")
    private Long projectId;
    
    @Schema(description = "父模块ID", example = "1")
    private Long parentId;
    
    @Schema(description = "模块路径", example = "/用户管理/登录模块")
    private String modulePath;
    
    @Schema(description = "模块层级深度", example = "2")
    private Integer depth;
    
    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;
    
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
    
    @Schema(description = "创建时间", example = "2024-01-15T09:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "是否为根模块", example = "false")
    private Boolean isRoot;
    
    @Schema(description = "是否为叶子模块", example = "true")
    private Boolean isLeaf;
    
    @Schema(description = "完整模块名称", example = "用户管理 > 登录模块")
    private String fullName;
    
    @Schema(description = "测试用例总数", example = "15")
    private Integer totalTestCaseCount;
    
    @Schema(description = "子模块列表")
    private List<TestModuleResponseDTO> children;
    
    /**
     * 从实体类转换为响应DTO
     */
    public static TestModuleResponseDTO from(TestModule testModule) {
        if (testModule == null) {
            return null;
        }
        
        TestModuleResponseDTOBuilder builder = TestModuleResponseDTO.builder()
                .id(testModule.getId())
                .name(testModule.getName())
                .description(testModule.getDescription())
                .projectId(testModule.getProjectId())
                .parentId(testModule.getParentId())
                .modulePath(testModule.getModulePath())
                .depth(testModule.getDepth())
                .sortOrder(testModule.getSortOrder())
                .enabled(testModule.getEnabled())
                .createdBy(testModule.getCreatedBy())
                .updatedBy(testModule.getUpdatedBy())
                .createdAt(testModule.getCreatedAt())
                .updatedAt(testModule.getUpdatedAt())
                .isRoot(testModule.isRoot())
                .isLeaf(testModule.isLeaf())
                .fullName(testModule.getFullName())
                .totalTestCaseCount(testModule.getTotalTestCaseCount());
        
        // 转换子模块（递归）
        if (testModule.getChildren() != null && !testModule.getChildren().isEmpty()) {
            List<TestModuleResponseDTO> childDTOs = testModule.getChildren().stream()
                    .map(TestModuleResponseDTO::from)
                    .collect(Collectors.toList());
            builder.children(childDTOs);
        }
        
        return builder.build();
    }
    
    /**
     * 创建简化版本（不包含子模块详情）
     */
    public static TestModuleResponseDTO fromSimple(TestModule testModule) {
        TestModuleResponseDTO dto = from(testModule);
        if (dto != null) {
            dto.setChildren(null); // 移除子模块详情以减少数据量
        }
        return dto;
    }
}