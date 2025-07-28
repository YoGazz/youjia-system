package com.yoga.youjia.service;

import com.yoga.youjia.common.enums.TestCasePriority;
import com.yoga.youjia.common.enums.TestCaseStatus;
import com.yoga.youjia.common.enums.TestCaseType;
import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.dto.request.CreateTestCaseRequestDTO;
import com.yoga.youjia.dto.request.TestCaseQueryRequestDTO;
import com.yoga.youjia.entity.TestCase;
import com.yoga.youjia.entity.TestModule;
import com.yoga.youjia.entity.TestStep;
import com.yoga.youjia.repository.TestCaseRepository;
import com.yoga.youjia.repository.TestModuleRepository;
import com.yoga.youjia.repository.TestStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 测试用例服务类
 * 
 * 提供测试用例的完整业务逻辑处理
 */
@Slf4j
@Service
@Transactional
public class TestCaseService {
    
    @Autowired
    private TestCaseRepository testCaseRepository;
    
    @Autowired
    private TestModuleRepository testModuleRepository;
    
    @Autowired
    private TestStepRepository testStepRepository;
    
    /**
     * 创建测试用例
     */
    public TestCase createTestCase(CreateTestCaseRequestDTO requestDTO, Long projectId, Long createdBy) {
        log.info("创建测试用例: projectId={}, moduleId={}, title={}", 
                projectId, requestDTO.getModuleId(), requestDTO.getTitle());
        
        // 验证模块是否存在
        TestModule testModule = testModuleRepository.findById(requestDTO.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, "测试模块不存在"));
        
        if (!testModule.getProjectId().equals(projectId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模块不属于指定项目");
        }
        
        // 生成用例编号
        String caseId = generateCaseId(projectId, requestDTO.getModuleId());
        
        // 检查用例编号是否重复
        if (testCaseRepository.existsByCaseIdAndEnabledTrue(caseId)) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "用例编号已存在: " + caseId);
        }
        
        // 获取下一个排序序号
        Integer sortOrder = testCaseRepository.findMaxSortOrderByModule(requestDTO.getModuleId()) + 1;
        
        // 构建测试用例
        TestCase testCase = TestCase.builder()
                .caseId(caseId)
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .preconditions(requestDTO.getPreconditions())
                .testData(requestDTO.getTestData())
                .expectedResult(requestDTO.getExpectedResult())
                .postconditions(requestDTO.getPostconditions())
                .type(requestDTO.getType() != null ? requestDTO.getType() : TestCaseType.FUNCTIONAL)
                .priority(requestDTO.getPriority() != null ? requestDTO.getPriority() : TestCasePriority.MEDIUM)
                .status(TestCaseStatus.DRAFT)
                .automated(requestDTO.getAutomated() != null ? requestDTO.getAutomated() : false)
                .automationScript(requestDTO.getAutomationScript())
                .projectId(projectId)
                .moduleId(requestDTO.getModuleId())
                .requirementId(requestDTO.getRequirementId())
                .sortOrder(sortOrder)
                .estimatedTime(requestDTO.getEstimatedTime() != null ? requestDTO.getEstimatedTime() : 5)
                .version(1)
                .enabled(true)
                .createdBy(createdBy)
                .build();
        
        // 设置标签
        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            testCase.setTagList(requestDTO.getTags());
        }
        
        // 保存测试用例
        testCase = testCaseRepository.save(testCase);
        
        // 创建测试步骤
        if (requestDTO.getTestSteps() != null && !requestDTO.getTestSteps().isEmpty()) {
            for (CreateTestCaseRequestDTO.CreateTestStepRequestDTO stepDTO : requestDTO.getTestSteps()) {
                TestStep testStep = TestStep.builder()
                        .testCaseId(testCase.getId())
                        .stepOrder(stepDTO.getStepOrder())
                        .stepDescription(stepDTO.getStepDescription())
                        .testData(stepDTO.getTestData())
                        .expectedResult(stepDTO.getExpectedResult())
                        .remark(stepDTO.getRemark())
                        .isKeyStep(stepDTO.getIsKeyStep() != null ? stepDTO.getIsKeyStep() : false)
                        .automated(stepDTO.getAutomated() != null ? stepDTO.getAutomated() : false)
                        .automationCode(stepDTO.getAutomationCode())
                        .estimatedTime(stepDTO.getEstimatedTime() != null ? stepDTO.getEstimatedTime() : 30)
                        .enabled(true)
                        .build();
                
                testStepRepository.save(testStep);
            }
        }
        
        log.info("测试用例创建成功: id={}, caseId={}", testCase.getId(), testCase.getCaseId());
        return testCase;
    }
    
    /**
     * 根据条件查询测试用例
     */
    @Transactional(readOnly = true)
    public Page<TestCase> queryTestCases(Long projectId, TestCaseQueryRequestDTO queryDTO) {
        log.debug("查询测试用例: projectId={}, query={}", projectId, queryDTO);
        
        // 构建分页参数
        Sort sort = buildSort(queryDTO.getSortBy(), queryDTO.getSortDirection());
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);
        
        // 如果有关键字搜索，使用全文搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            return testCaseRepository.searchByKeyword(projectId, queryDTO.getKeyword(), pageable);
        }
        
        // 否则使用条件查询
        return testCaseRepository.findByConditions(
                projectId,
                queryDTO.getTitle(),
                queryDTO.getModuleId(),
                queryDTO.getType(),
                queryDTO.getPriority(),
                queryDTO.getStatus(),
                queryDTO.getAutomated(),
                queryDTO.getCreatedBy(),
                queryDTO.getTag(),
                pageable
        );
    }
    
    /**
     * 根据ID获取测试用例详情
     */
    @Transactional(readOnly = true)
    public TestCase getTestCaseById(Long id) {
        return testCaseRepository.findById(id)
                .filter(TestCase::getEnabled)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, "测试用例不存在"));
    }
    
    /**
     * 根据用例编号获取测试用例
     */
    @Transactional(readOnly = true)
    public TestCase getTestCaseByCaseId(String caseId) {
        return testCaseRepository.findByCaseIdAndEnabledTrue(caseId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, "测试用例不存在"));
    }
    
    /**
     * 更新测试用例
     */
    public TestCase updateTestCase(Long id, CreateTestCaseRequestDTO requestDTO, Long updatedBy) {
        log.info("更新测试用例: id={}, title={}", id, requestDTO.getTitle());
        
        TestCase testCase = getTestCaseById(id);
        
        // 检查是否可以编辑
        if (!testCase.canEdit()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "当前状态不允许编辑: " + testCase.getStatus());
        }
        
        // 更新基本信息
        testCase.setTitle(requestDTO.getTitle());
        testCase.setDescription(requestDTO.getDescription());
        testCase.setPreconditions(requestDTO.getPreconditions());
        testCase.setTestData(requestDTO.getTestData());
        testCase.setExpectedResult(requestDTO.getExpectedResult());
        testCase.setPostconditions(requestDTO.getPostconditions());
        testCase.setType(requestDTO.getType() != null ? requestDTO.getType() : testCase.getType());
        testCase.setPriority(requestDTO.getPriority() != null ? requestDTO.getPriority() : testCase.getPriority());
        testCase.setAutomated(requestDTO.getAutomated() != null ? requestDTO.getAutomated() : testCase.getAutomated());
        testCase.setAutomationScript(requestDTO.getAutomationScript());
        testCase.setRequirementId(requestDTO.getRequirementId());
        testCase.setEstimatedTime(requestDTO.getEstimatedTime() != null ? requestDTO.getEstimatedTime() : testCase.getEstimatedTime());
        testCase.setUpdatedBy(updatedBy);
        
        // 更新标签
        if (requestDTO.getTags() != null) {
            testCase.setTagList(requestDTO.getTags());
        }
        
        // 更新测试步骤
        if (requestDTO.getTestSteps() != null) {
            // 删除现有步骤
            testStepRepository.softDeleteByTestCaseId(testCase.getId());
            
            // 创建新步骤
            for (CreateTestCaseRequestDTO.CreateTestStepRequestDTO stepDTO : requestDTO.getTestSteps()) {
                TestStep testStep = TestStep.builder()
                        .testCaseId(testCase.getId())
                        .stepOrder(stepDTO.getStepOrder())
                        .stepDescription(stepDTO.getStepDescription())
                        .testData(stepDTO.getTestData())
                        .expectedResult(stepDTO.getExpectedResult())
                        .remark(stepDTO.getRemark())
                        .isKeyStep(stepDTO.getIsKeyStep() != null ? stepDTO.getIsKeyStep() : false)
                        .automated(stepDTO.getAutomated() != null ? stepDTO.getAutomated() : false)
                        .automationCode(stepDTO.getAutomationCode())
                        .estimatedTime(stepDTO.getEstimatedTime() != null ? stepDTO.getEstimatedTime() : 30)
                        .enabled(true)
                        .build();
                
                testStepRepository.save(testStep);
            }
        }
        
        testCase = testCaseRepository.save(testCase);
        log.info("测试用例更新成功: id={}", testCase.getId());
        return testCase;
    }
    
    /**
     * 删除测试用例（软删除）
     */
    public void deleteTestCase(Long id, Long deletedBy) {
        log.info("删除测试用例: id={}", id);
        
        TestCase testCase = getTestCaseById(id);
        
        // 检查是否可以删除
        if (testCase.getStatus() == TestCaseStatus.UNDER_REVIEW) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "审核中的用例不能删除");
        }
        
        // 软删除
        testCase.setEnabled(false);
        testCase.setUpdatedBy(deletedBy);
        testCaseRepository.save(testCase);
        
        // 删除关联的测试步骤
        testStepRepository.softDeleteByTestCaseId(id);
        
        log.info("测试用例删除成功: id={}", id);
    }
    
    /**
     * 提交测试用例审核
     */
    public TestCase submitForReview(Long id, Long submittedBy) {
        log.info("提交测试用例审核: id={}", id);
        
        TestCase testCase = getTestCaseById(id);
        testCase.submitForReview();
        testCase.setUpdatedBy(submittedBy);
        
        testCase = testCaseRepository.save(testCase);
        log.info("测试用例提交审核成功: id={}, status={}", testCase.getId(), testCase.getStatus());
        return testCase;
    }
    
    /**
     * 审核通过测试用例
     */
    public TestCase approveTestCase(Long id, Long reviewerId, String comment) {
        log.info("审核通过测试用例: id={}, reviewerId={}", id, reviewerId);
        
        TestCase testCase = getTestCaseById(id);
        testCase.approveReview(reviewerId, comment);
        
        testCase = testCaseRepository.save(testCase);
        log.info("测试用例审核通过: id={}, status={}", testCase.getId(), testCase.getStatus());
        return testCase;
    }
    
    /**
     * 审核拒绝测试用例
     */
    public TestCase rejectTestCase(Long id, Long reviewerId, String comment) {
        log.info("审核拒绝测试用例: id={}, reviewerId={}", id, reviewerId);
        
        TestCase testCase = getTestCaseById(id);
        testCase.rejectReview(reviewerId, comment);
        
        testCase = testCaseRepository.save(testCase);
        log.info("测试用例审核拒绝: id={}, status={}", testCase.getId(), testCase.getStatus());
        return testCase;
    }
    
    /**
     * 复制测试用例
     */
    public TestCase copyTestCase(Long id, String newTitle, Long createdBy) {
        log.info("复制测试用例: id={}, newTitle={}", id, newTitle);
        
        TestCase originalCase = getTestCaseById(id);
        
        // 生成新的用例编号
        String newCaseId = generateCaseId(originalCase.getProjectId(), originalCase.getModuleId());
        
        // 复制用例
        TestCase newCase = originalCase.copy(newTitle, newCaseId);
        newCase.setCreatedBy(createdBy);
        
        newCase = testCaseRepository.save(newCase);
        log.info("测试用例复制成功: originalId={}, newId={}, newCaseId={}", 
                id, newCase.getId(), newCase.getCaseId());
        return newCase;
    }
    
    /**
     * 批量更新测试用例状态
     */
    public void batchUpdateStatus(List<Long> ids, TestCaseStatus status, Long updatedBy) {
        log.info("批量更新测试用例状态: ids={}, status={}", ids, status);
        
        testCaseRepository.batchUpdateStatus(ids, status, updatedBy);
        log.info("批量更新测试用例状态成功: count={}", ids.size());
    }
    
    /**
     * 获取项目测试用例统计信息
     */
    @Transactional(readOnly = true)
    public TestCaseStatistics getProjectStatistics(Long projectId) {
        log.debug("获取项目测试用例统计: projectId={}", projectId);
        
        TestCaseStatistics statistics = new TestCaseStatistics();
        
        // 总用例数
        statistics.setTotalCount(testCaseRepository.countByProjectIdAndEnabledTrue(projectId));
        
        // 自动化用例数
        statistics.setAutomatedCount(testCaseRepository.countByProjectIdAndAutomatedTrueAndEnabledTrue(projectId));
        
        // 按状态统计
        List<Object[]> statusStats = testCaseRepository.countByProjectIdAndStatusGroupByStatus(projectId);
        for (Object[] stat : statusStats) {
            TestCaseStatus status = (TestCaseStatus) stat[0];
            Long count = (Long) stat[1];
            statistics.addStatusCount(status, count);
        }
        
        // 按类型统计
        List<Object[]> typeStats = testCaseRepository.countByProjectIdAndTypeGroupByType(projectId);
        for (Object[] stat : typeStats) {
            TestCaseType type = (TestCaseType) stat[0];
            Long count = (Long) stat[1];
            statistics.addTypeCount(type, count);
        }
        
        // 按优先级统计
        List<Object[]> priorityStats = testCaseRepository.countByProjectIdAndPriorityGroupByPriority(projectId);
        for (Object[] stat : priorityStats) {
            TestCasePriority priority = (TestCasePriority) stat[0];
            Long count = (Long) stat[1];
            statistics.addPriorityCount(priority, count);
        }
        
        return statistics;
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 生成用例编号
     */
    private String generateCaseId(Long projectId, Long moduleId) {
        // 简化版本：TC_项目ID_模块ID_序号
        String prefix = String.format("TC_%d_%d_", projectId, moduleId);
        Integer nextNumber = testCaseRepository.generateNextCaseNumber(prefix);
        return prefix + String.format("%03d", nextNumber);
    }
    
    /**
     * 构建排序对象
     */
    private Sort buildSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        String sortField = StringUtils.hasText(sortBy) ? sortBy : "sortOrder";
        return Sort.by(direction, sortField).and(Sort.by(Sort.Direction.ASC, "id"));
    }
    
    /**
     * 测试用例统计信息类
     */
    public static class TestCaseStatistics {
        private Long totalCount = 0L;
        private Long automatedCount = 0L;
        private java.util.Map<TestCaseStatus, Long> statusCounts = new java.util.HashMap<>();
        private java.util.Map<TestCaseType, Long> typeCounts = new java.util.HashMap<>();
        private java.util.Map<TestCasePriority, Long> priorityCounts = new java.util.HashMap<>();
        
        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        
        public Long getAutomatedCount() { return automatedCount; }
        public void setAutomatedCount(Long automatedCount) { this.automatedCount = automatedCount; }
        
        public java.util.Map<TestCaseStatus, Long> getStatusCounts() { return statusCounts; }
        public void addStatusCount(TestCaseStatus status, Long count) { this.statusCounts.put(status, count); }
        
        public java.util.Map<TestCaseType, Long> getTypeCounts() { return typeCounts; }
        public void addTypeCount(TestCaseType type, Long count) { this.typeCounts.put(type, count); }
        
        public java.util.Map<TestCasePriority, Long> getPriorityCounts() { return priorityCounts; }
        public void addPriorityCount(TestCasePriority priority, Long count) { this.priorityCounts.put(priority, count); }
        
        public Double getAutomationRate() {
            if (totalCount == 0) return 0.0;
            return (automatedCount.doubleValue() / totalCount.doubleValue()) * 100;
        }
    }
}