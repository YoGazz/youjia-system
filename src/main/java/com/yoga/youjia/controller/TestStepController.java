package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.dto.request.CreateTestStepRequestDTO;
import com.yoga.youjia.dto.response.TestStepResponseDTO;
import com.yoga.youjia.entity.TestStep;
import com.yoga.youjia.service.TestStepService;
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
 * 测试步骤管理控制器
 * 
 * 提供测试步骤的详细管理功能
 */
@Tag(name = "测试步骤管理", description = "测试用例步骤的详细管理")
@RestController
@RequestMapping("/api/projects/{projectId}/test-cases/{testCaseId}/steps")
@Slf4j
public class TestStepController {
    
    @Autowired
    private TestStepService testStepService;
    
    /**
     * 创建测试步骤
     */
    @Operation(summary = "创建测试步骤", description = "为测试用例添加新的测试步骤")
    @PostMapping
    public ApiResponse<TestStepResponseDTO> createTestStep(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "步骤信息", required = true) @Valid @RequestBody CreateTestStepRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("创建测试步骤: projectId={}, testCaseId={}, stepOrder={}, user={}", 
                projectId, testCaseId, requestDTO.getStepOrder(), userDetails.getUsername());
        
        TestStep testStep = testStepService.createTestStep(requestDTO, testCaseId);
        TestStepResponseDTO responseDTO = TestStepResponseDTO.from(testStep);
        
        return ApiResponse.success(responseDTO, "测试步骤创建成功");
    }
    
    /**
     * 获取测试用例的所有步骤
     */
    @Operation(summary = "获取测试用例步骤列表", description = "获取指定测试用例的所有测试步骤")
    @GetMapping
    public ApiResponse<List<TestStepResponseDTO>> getTestSteps(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId) {
        
        log.debug("获取测试步骤列表: projectId={}, testCaseId={}", projectId, testCaseId);
        
        List<TestStep> testSteps = testStepService.getTestStepsByTestCaseId(testCaseId);
        List<TestStepResponseDTO> responseDTOs = testSteps.stream()
                .map(TestStepResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, "获取成功");
    }
    
    /**
     * 获取测试步骤详情
     */
    @Operation(summary = "获取测试步骤详情", description = "根据ID获取测试步骤的详细信息")
    @GetMapping("/{stepId}")
    public ApiResponse<TestStepResponseDTO> getTestStepById(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "步骤ID", required = true) @PathVariable Long stepId) {
        
        log.debug("获取测试步骤详情: projectId={}, testCaseId={}, stepId={}", projectId, testCaseId, stepId);
        
        TestStep testStep = testStepService.getTestStepById(stepId);
        
        // 验证步骤是否属于指定测试用例
        if (!testStep.getTestCaseId().equals(testCaseId)) {
            return ApiResponse.error(ErrorCode.PARAM_INVALID, "测试步骤不属于指定测试用例");
        }
        
        TestStepResponseDTO responseDTO = TestStepResponseDTO.from(testStep);
        return ApiResponse.success(responseDTO, "获取成功");
    }
    
    /**
     * 更新测试步骤
     */
    @Operation(summary = "更新测试步骤", description = "更新测试步骤的详细信息")
    @PutMapping("/{stepId}")
    public ApiResponse<TestStepResponseDTO> updateTestStep(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "步骤ID", required = true) @PathVariable Long stepId,
            @Parameter(description = "更新信息", required = true) @Valid @RequestBody CreateTestStepRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("更新测试步骤: projectId={}, testCaseId={}, stepId={}, user={}", 
                projectId, testCaseId, stepId, userDetails.getUsername());
        
        TestStep testStep = testStepService.updateTestStep(stepId, requestDTO);
        TestStepResponseDTO responseDTO = TestStepResponseDTO.from(testStep);
        
        return ApiResponse.success(responseDTO, "测试步骤更新成功");
    }
    
    /**
     * 删除测试步骤
     */
    @Operation(summary = "删除测试步骤", description = "删除指定的测试步骤")
    @DeleteMapping("/{stepId}")
    public ApiResponse<Void> deleteTestStep(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "步骤ID", required = true) @PathVariable Long stepId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("删除测试步骤: projectId={}, testCaseId={}, stepId={}, user={}", 
                projectId, testCaseId, stepId, userDetails.getUsername());
        
        testStepService.deleteTestStep(stepId);
        return ApiResponse.success(null, "测试步骤删除成功");
    }
    
    /**
     * 调整步骤顺序
     */
    @Operation(summary = "调整步骤顺序", description = "调整测试步骤的执行顺序")
    @PostMapping("/{stepId}/reorder")
    public ApiResponse<Void> reorderTestStep(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "步骤ID", required = true) @PathVariable Long stepId,
            @Parameter(description = "新的序号", required = true) @RequestParam Integer newOrder,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("调整步骤顺序: projectId={}, testCaseId={}, stepId={}, newOrder={}, user={}", 
                projectId, testCaseId, stepId, newOrder, userDetails.getUsername());
        
        testStepService.reorderTestStep(stepId, newOrder);
        return ApiResponse.success(null, "步骤顺序调整成功");
    }
    
    /**
     * 获取关键步骤
     */
    @Operation(summary = "获取关键步骤", description = "获取测试用例中标记为关键的步骤")
    @GetMapping("/key-steps")
    public ApiResponse<List<TestStepResponseDTO>> getKeySteps(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId) {
        
        log.debug("获取关键步骤: projectId={}, testCaseId={}", projectId, testCaseId);
        
        List<TestStep> keySteps = testStepService.getKeyStepsByTestCaseId(testCaseId);
        List<TestStepResponseDTO> responseDTOs = keySteps.stream()
                .map(TestStepResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, "获取成功");
    }
    
    /**
     * 获取自动化步骤
     */
    @Operation(summary = "获取自动化步骤", description = "获取测试用例中可自动化执行的步骤")
    @GetMapping("/automated-steps")
    public ApiResponse<List<TestStepResponseDTO>> getAutomatedSteps(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId) {
        
        log.debug("获取自动化步骤: projectId={}, testCaseId={}", projectId, testCaseId);
        
        List<TestStep> automatedSteps = testStepService.getAutomatedStepsByTestCaseId(testCaseId);
        List<TestStepResponseDTO> responseDTOs = automatedSteps.stream()
                .map(TestStepResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, "获取成功");
    }
    
    /**
     * 批量创建测试步骤
     */
    @Operation(summary = "批量创建测试步骤", description = "批量为测试用例添加多个测试步骤")
    @PostMapping("/batch")
    public ApiResponse<List<TestStepResponseDTO>> batchCreateTestSteps(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "步骤列表", required = true) @Valid @RequestBody List<CreateTestStepRequestDTO> requestDTOs,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("批量创建测试步骤: projectId={}, testCaseId={}, count={}, user={}", 
                projectId, testCaseId, requestDTOs.size(), userDetails.getUsername());
        
        List<TestStep> testSteps = testStepService.batchCreateTestSteps(requestDTOs, testCaseId);
        List<TestStepResponseDTO> responseDTOs = testSteps.stream()
                .map(TestStepResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(responseDTOs, String.format("成功创建 %d 个测试步骤", testSteps.size()));
    }
}