package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.enums.TestCaseStatus;
import com.yoga.youjia.dto.request.CreateTestCaseRequestDTO;
import com.yoga.youjia.dto.request.TestCaseQueryRequestDTO;
import com.yoga.youjia.dto.response.PageResponseDTO;
import com.yoga.youjia.dto.response.TestCaseResponseDTO;
import com.yoga.youjia.entity.TestCase;
import com.yoga.youjia.service.TestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例管理控制器
 * 
 * 提供测试用例的完整REST API接口
 */
@Tag(name = "测试用例管理", description = "测试用例的增删改查、审核流程等功能")
@RestController
@RequestMapping("/api/projects/{projectId}/test-cases")
@Slf4j
public class TestCaseController {
    
    @Autowired
    private TestCaseService testCaseService;
    
    /**
     * 创建测试用例
     */
    @Operation(summary = "创建测试用例", description = "在指定项目下创建新的测试用例")
    @PostMapping
    public ApiResponse<TestCaseResponseDTO> createTestCase(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例信息", required = true) @Valid @RequestBody CreateTestCaseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("创建测试用例请求: projectId={}, title={}, user={}", 
                projectId, requestDTO.getTitle(), userDetails.getUsername());
        
        // 获取当前用户ID（这里简化处理，实际项目中需要从UserDetails中获取）
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestCase testCase = testCaseService.createTestCase(requestDTO, projectId, currentUserId);
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        
        return ApiResponse.success(responseDTO, "测试用例创建成功");
    }
    
    /**
     * 分页查询测试用例
     */
    @Operation(summary = "分页查询测试用例", description = "根据条件分页查询项目下的测试用例")
    @GetMapping
    public ApiResponse<PageResponseDTO<TestCaseResponseDTO>> queryTestCases(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "查询条件") @ModelAttribute TestCaseQueryRequestDTO queryDTO) {
        
        log.debug("查询测试用例: projectId={}, query={}", projectId, queryDTO);
        
        Page<TestCase> testCasePage = testCaseService.queryTestCases(projectId, queryDTO);
        
        // 转换为响应DTO（使用简化版本以提高性能）
        List<TestCaseResponseDTO> testCaseDTOs = testCasePage.getContent().stream()
                .map(TestCaseResponseDTO::fromSimple)
                .collect(Collectors.toList());
        
        PageResponseDTO<TestCaseResponseDTO> pageResponse = PageResponseDTO.<TestCaseResponseDTO>builder()
                .content(testCaseDTOs)
                .page(testCasePage.getNumber())
                .size(testCasePage.getSize())
                .totalElements(testCasePage.getTotalElements())
                .totalPages(testCasePage.getTotalPages())
                .first(testCasePage.isFirst())
                .last(testCasePage.isLast())
                .build();
        
        return ApiResponse.success(pageResponse, "查询成功");
    }
    
    /**
     * 获取测试用例详情
     */
    @Operation(summary = "获取测试用例详情", description = "根据ID获取测试用例的详细信息，包括测试步骤")
    @GetMapping("/{testCaseId}")
    public ApiResponse<TestCaseResponseDTO> getTestCaseById(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId) {
        
        log.debug("获取测试用例详情: projectId={}, testCaseId={}", projectId, testCaseId);
        
        TestCase testCase = testCaseService.getTestCaseById(testCaseId);
        
        // 验证用例是否属于指定项目
        if (!testCase.getProjectId().equals(projectId)) {
            return ApiResponse.error(ErrorCode.PARAM_INVALID, "测试用例不属于指定项目");
        }
        
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        return ApiResponse.success(responseDTO, "获取成功");
    }
    
    /**
     * 根据用例编号获取测试用例
     */
    @Operation(summary = "根据用例编号获取测试用例", description = "通过用例编号查找测试用例")
    @GetMapping("/by-case-id/{caseId}")
    public ApiResponse<TestCaseResponseDTO> getTestCaseByCaseId(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "用例编号", required = true) @PathVariable String caseId) {
        
        log.debug("根据编号获取测试用例: projectId={}, caseId={}", projectId, caseId);
        
        TestCase testCase = testCaseService.getTestCaseByCaseId(caseId);
        
        // 验证用例是否属于指定项目
        if (!testCase.getProjectId().equals(projectId)) {
            return ApiResponse.error(ErrorCode.PARAM_INVALID, "测试用例不属于指定项目");
        }
        
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        return ApiResponse.success(responseDTO, "获取成功");
    }
    
    /**
     * 更新测试用例
     */
    @Operation(summary = "更新测试用例", description = "更新测试用例的基本信息和测试步骤")
    @PutMapping("/{testCaseId}")
    public ApiResponse<TestCaseResponseDTO> updateTestCase(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "更新信息", required = true) @Valid @RequestBody CreateTestCaseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("更新测试用例: projectId={}, testCaseId={}, user={}", 
                projectId, testCaseId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestCase testCase = testCaseService.updateTestCase(testCaseId, requestDTO, currentUserId);
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        
        return ApiResponse.success(responseDTO, "测试用例更新成功");
    }
    
    /**
     * 删除测试用例
     */
    @Operation(summary = "删除测试用例", description = "软删除测试用例，不会物理删除数据")
    @DeleteMapping("/{testCaseId}")
    public ApiResponse<Void> deleteTestCase(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("删除测试用例: projectId={}, testCaseId={}, user={}", 
                projectId, testCaseId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        testCaseService.deleteTestCase(testCaseId, currentUserId);
        return ApiResponse.success(null, "测试用例删除成功");
    }
    
    /**
     * 复制测试用例
     */
    @Operation(summary = "复制测试用例", description = "复制现有测试用例，创建一个新的副本")
    @PostMapping("/{testCaseId}/copy")
    public ApiResponse<TestCaseResponseDTO> copyTestCase(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "源测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "新用例标题", required = true) @RequestParam String newTitle,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("复制测试用例: projectId={}, sourceId={}, newTitle={}, user={}", 
                projectId, testCaseId, newTitle, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestCase newTestCase = testCaseService.copyTestCase(testCaseId, newTitle, currentUserId);
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(newTestCase);
        
        return ApiResponse.success(responseDTO, "测试用例复制成功");
    }
    
    /**
     * 提交测试用例审核
     */
    @Operation(summary = "提交测试用例审核", description = "将测试用例提交给审核人员进行审核")
    @PostMapping("/{testCaseId}/submit-review")
    public ApiResponse<TestCaseResponseDTO> submitForReview(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("提交测试用例审核: projectId={}, testCaseId={}, user={}", 
                projectId, testCaseId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestCase testCase = testCaseService.submitForReview(testCaseId, currentUserId);
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        
        return ApiResponse.success(responseDTO, "测试用例已提交审核");
    }
    
    /**
     * 审核通过测试用例
     */
    @Operation(summary = "审核通过测试用例", description = "审核人员审核通过测试用例")
    @PostMapping("/{testCaseId}/approve")
    public ApiResponse<TestCaseResponseDTO> approveTestCase(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "审核意见") @RequestParam(required = false) String comment,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("审核通过测试用例: projectId={}, testCaseId={}, reviewer={}", 
                projectId, testCaseId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestCase testCase = testCaseService.approveTestCase(testCaseId, currentUserId, comment);
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        
        return ApiResponse.success(responseDTO, "测试用例审核通过");
    }
    
    /**
     * 审核拒绝测试用例
     */
    @Operation(summary = "审核拒绝测试用例", description = "审核人员拒绝测试用例，需要修改后重新提交")
    @PostMapping("/{testCaseId}/reject")
    public ApiResponse<TestCaseResponseDTO> rejectTestCase(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID", required = true) @PathVariable Long testCaseId,
            @Parameter(description = "拒绝原因", required = true) @RequestParam String comment,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("审核拒绝测试用例: projectId={}, testCaseId={}, reviewer={}", 
                projectId, testCaseId, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        TestCase testCase = testCaseService.rejectTestCase(testCaseId, currentUserId, comment);
        TestCaseResponseDTO responseDTO = TestCaseResponseDTO.from(testCase);
        
        return ApiResponse.success(responseDTO, "测试用例已拒绝");
    }
    
    /**
     * 批量更新测试用例状态
     */
    @Operation(summary = "批量更新测试用例状态", description = "批量修改多个测试用例的状态")
    @PostMapping("/batch-update-status")
    public ApiResponse<Void> batchUpdateStatus(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "测试用例ID列表", required = true) @RequestParam List<Long> testCaseIds,
            @Parameter(description = "目标状态", required = true) @RequestParam TestCaseStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("批量更新测试用例状态: projectId={}, count={}, status={}, user={}", 
                projectId, testCaseIds.size(), status, userDetails.getUsername());
        
        Long currentUserId = 1L; // TODO: 从认证信息中获取真实用户ID
        
        testCaseService.batchUpdateStatus(testCaseIds, status, currentUserId);
        return ApiResponse.success(null, String.format("成功更新 %d 个测试用例状态", testCaseIds.size()));
    }
    
    /**
     * 获取项目测试用例统计信息
     */
    @Operation(summary = "获取项目测试用例统计", description = "获取项目下测试用例的统计分析数据")
    @GetMapping("/statistics")
    public ApiResponse<TestCaseService.TestCaseStatistics> getProjectStatistics(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId) {
        
        log.debug("获取项目测试用例统计: projectId={}", projectId);
        
        TestCaseService.TestCaseStatistics statistics = testCaseService.getProjectStatistics(projectId);
        return ApiResponse.success(statistics, "统计信息获取成功");
    }
    
    /**
     * 搜索测试用例
     */
    @Operation(summary = "搜索测试用例", description = "根据关键字全文搜索测试用例")
    @GetMapping("/search")
    public ApiResponse<PageResponseDTO<TestCaseResponseDTO>> searchTestCases(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "搜索关键字", required = true) @RequestParam String keyword,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") Integer size) {
        
        log.debug("搜索测试用例: projectId={}, keyword={}", projectId, keyword);
        
        TestCaseQueryRequestDTO queryDTO = TestCaseQueryRequestDTO.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
        
        Page<TestCase> testCasePage = testCaseService.queryTestCases(projectId, queryDTO);
        
        List<TestCaseResponseDTO> testCaseDTOs = testCasePage.getContent().stream()
                .map(TestCaseResponseDTO::fromSimple)
                .collect(Collectors.toList());
        
        PageResponseDTO<TestCaseResponseDTO> pageResponse = PageResponseDTO.<TestCaseResponseDTO>builder()
                .content(testCaseDTOs)
                .page(testCasePage.getNumber())
                .size(testCasePage.getSize())
                .totalElements(testCasePage.getTotalElements())
                .totalPages(testCasePage.getTotalPages())
                .first(testCasePage.isFirst())
                .last(testCasePage.isLast())
                .build();
        
        return ApiResponse.success(pageResponse, "搜索完成");
    }
    
    /**
     * 获取模块下的测试用例
     */
    @Operation(summary = "获取模块下的测试用例", description = "获取指定模块下的所有测试用例")
    @GetMapping("/by-module/{moduleId}")
    public ApiResponse<PageResponseDTO<TestCaseResponseDTO>> getTestCasesByModule(
            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "模块ID", required = true) @PathVariable Long moduleId,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") Integer size) {
        
        log.debug("获取模块测试用例: projectId={}, moduleId={}", projectId, moduleId);
        
        TestCaseQueryRequestDTO queryDTO = TestCaseQueryRequestDTO.builder()
                .moduleId(moduleId)
                .page(page)
                .size(size)
                .build();
        
        Page<TestCase> testCasePage = testCaseService.queryTestCases(projectId, queryDTO);
        
        List<TestCaseResponseDTO> testCaseDTOs = testCasePage.getContent().stream()
                .map(TestCaseResponseDTO::fromSimple)
                .collect(Collectors.toList());
        
        PageResponseDTO<TestCaseResponseDTO> pageResponse = PageResponseDTO.<TestCaseResponseDTO>builder()
                .content(testCaseDTOs)
                .page(testCasePage.getNumber())
                .size(testCasePage.getSize())
                .totalElements(testCasePage.getTotalElements())
                .totalPages(testCasePage.getTotalPages())
                .first(testCasePage.isFirst())
                .last(testCasePage.isLast())
                .build();
        
        return ApiResponse.success(pageResponse, "获取成功");
    }
}