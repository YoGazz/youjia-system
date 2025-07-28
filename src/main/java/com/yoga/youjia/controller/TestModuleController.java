package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.dto.request.CreateTestModuleRequestDTO;
import com.yoga.youjia.dto.response.TestModuleResponseDTO;
import com.yoga.youjia.entity.TestModule;
import com.yoga.youjia.service.TestModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试模块管理控制器
 * 
 * 提供测试模块的层级管理功能
 */
@Tag(name = "测试模块管理", description = "测试模块的树形结构管理")
@RestController
@RequestMapping("/api/projects/{projectId}/test-modules")
@Slf4j
public class TestModuleController {
    
    @Autowired
    private TestModuleService testModuleService;
    
    /**
     * 创建测试模块
     */
    @Operation(summary = "创建测试模块", description = "在指定项目下创建新的测试模块")
    @PostMapping
    public ApiResponse<TestModuleResponseDTO> createTestModule(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块信息", required = true) @Valid @RequestBody CreateTestModuleRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("创建测试模块: projectId={}, name={}, parentId={}, user={}", 
                projectId, requestDTO.getName(), requestDTO.getParentId(), userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestModule testModule = testModuleService.createTestModule(requestDTO, projectId, currentUserId);
        TestModuleResponseDTO responseDTO = TestModuleResponseDTO.from(testModule);
        
        return ApiResponse.success(responseDTO, "测试模块创建成功");
    }
    
    /**
     * 获取项目模块树
     */
    @Operation(summary = "获取项目模块树", description = "获取项目下的完整模块树形结构")
    @GetMapping("/tree")
    public ApiResponse<List<TestModuleResponseDTO>> getModuleTree(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId) {
        
        log.debug("获取项目模块树: projectId={}", projectId);
        
        List<TestModule> moduleTree = testModuleService.getProjectModuleTree(projectId);
        List<TestModuleResponseDTO> responseDTOs = moduleTree.stream()
                .map(TestModuleResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, "获取成功");
    }
    
    /**
     * 获取根模块列表
     */
    @Operation(summary = "获取根模块列表", description = "获取项目下的所有根模块")
    @GetMapping("/roots")
    public ApiResponse<List<TestModuleResponseDTO>> getRootModules(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId) {
        
        log.debug("获取根模块列表: projectId={}", projectId);
        
        List<TestModule> rootModules = testModuleService.getRootModules(projectId);
        List<TestModuleResponseDTO> responseDTOs = rootModules.stream()
                .map(TestModuleResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, "获取成功");
    }
    
    /**
     * 获取子模块列表
     */
    @Operation(summary = "获取子模块列表", description = "获取指定模块的直接子模块")
    @GetMapping("/{moduleId}/children")
    public ApiResponse<List<TestModuleResponseDTO>> getChildModules(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "父模块ID", required = true) @PathVariable Long moduleId) {
        
        log.debug("获取子模块列表: projectId={}, parentId={}", projectId, moduleId);
        
        List<TestModule> childModules = testModuleService.getChildModules(projectId, moduleId);
        List<TestModuleResponseDTO> responseDTOs = childModules.stream()
                .map(TestModuleResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, "获取成功");
    }
    
    /**
     * 获取模块详情
     */
    @Operation(summary = "获取模块详情", description = "根据ID获取测试模块的详细信息")
    @GetMapping("/{moduleId}")
    public ApiResponse<TestModuleResponseDTO> getTestModuleById(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId) {
        
        log.debug("获取模块详情: projectId={}, moduleId={}", projectId, moduleId);
        
        TestModule testModule = testModuleService.getTestModuleById(moduleId);
        
        // 验证模块是否属于指定项目
        if (!testModule.getProjectId().equals(projectId)) {
            return ApiResponse.error(ErrorCode.PARAM_INVALID, "测试模块不属于指定项目");
        }
        
        TestModuleResponseDTO responseDTO = TestModuleResponseDTO.from(testModule);
        return ApiResponse.success(responseDTO, "获取成功");
    }
    
    /**
     * 更新测试模块
     */
    @Operation(summary = "更新测试模块", description = "更新测试模块的基本信息")
    @PutMapping("/{moduleId}")
    public ApiResponse<TestModuleResponseDTO> updateTestModule(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId,
            @Parameter(description = "更新信息", required = true) @Valid @RequestBody CreateTestModuleRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("更新测试模块: projectId={}, moduleId={}, name={}, user={}", 
                projectId, moduleId, requestDTO.getName(), userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestModule testModule = testModuleService.updateTestModule(moduleId, requestDTO, currentUserId);
        TestModuleResponseDTO responseDTO = TestModuleResponseDTO.from(testModule);
        
        return ApiResponse.success(responseDTO, "测试模块更新成功");
    }
    
    /**
     * 删除测试模块
     */
    @Operation(summary = "删除测试模块", description = "删除测试模块及其下的所有子模块和测试用例")
    @DeleteMapping("/{moduleId}")
    public ApiResponse<Void> deleteTestModule(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("删除测试模块: projectId={}, moduleId={}, user={}", 
                projectId, moduleId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        testModuleService.deleteTestModule(moduleId, currentUserId);
        return ApiResponse.success(null, "测试模块删除成功");
    }
    
    /**
     * 移动模块
     */
    @Operation(summary = "移动模块", description = "将模块移动到新的父模块下")
    @PostMapping("/{moduleId}/move")
    public ApiResponse<TestModuleResponseDTO> moveModule(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId,
            @Parameter(description = "新父模块ID") @RequestParam(required = false) Long newParentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("移动测试模块: projectId={}, moduleId={}, newParentId={}, user={}", 
                projectId, moduleId, newParentId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestModule testModule = testModuleService.moveModule(moduleId, newParentId, currentUserId);
        TestModuleResponseDTO responseDTO = TestModuleResponseDTO.from(testModule);
        
        return ApiResponse.success(responseDTO, "模块移动成功");
    }
    
    /**
     * 调整模块排序
     */
    @Operation(summary = "调整模块排序", description = "调整同级模块的排序顺序")
    @PostMapping("/{moduleId}/reorder")
    public ApiResponse<Void> reorderModule(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId,
            @Parameter(description = "新排序位置", required = true) @RequestParam Integer newOrder,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("调整模块排序: projectId={}, moduleId={}, newOrder={}, user={}", 
                projectId, moduleId, newOrder, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        testModuleService.reorderModule(moduleId, newOrder, currentUserId);
        return ApiResponse.success(null, "排序调整成功");
    }
    
    /**
     * 获取模块统计信息
     */
    @Operation(summary = "获取模块统计信息", description = "获取模块下测试用例的统计信息")
    @GetMapping("/{moduleId}/statistics")
    public ApiResponse<TestModuleService.ModuleStatistics> getModuleStatistics(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId) {
        
        log.debug("获取模块统计信息: projectId={}, moduleId={}", projectId, moduleId);
        
        TestModuleService.ModuleStatistics statistics = testModuleService.getModuleStatistics(moduleId);
        return ApiResponse.success(statistics, "统计信息获取成功");
    }
}