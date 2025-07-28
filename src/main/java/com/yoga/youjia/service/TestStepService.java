package com.yoga.youjia.service;

import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import com.yoga.youjia.dto.request.CreateTestStepRequestDTO;
import com.yoga.youjia.entity.TestStep;
import com.yoga.youjia.repository.TestStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试步骤服务类
 * 
 * 提供测试步骤的详细管理功能
 */
@Slf4j
@Service
@Transactional
public class TestStepService {
    
    @Autowired
    private TestStepRepository testStepRepository;
    
    /**
     * 创建测试步骤
     */
    public TestStep createTestStep(CreateTestStepRequestDTO requestDTO, Long testCaseId) {
        log.info("创建测试步骤: testCaseId={}, stepOrder={}", testCaseId, requestDTO.getStepOrder());
        
        // 检查步骤序号是否重复
        if (testStepRepository.existsByTestCaseIdAndStepOrderAndEnabledTrueAndIdNot(
                testCaseId, requestDTO.getStepOrder(), -1L)) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "步骤序号已存在: " + requestDTO.getStepOrder());
        }
        
        // 构建测试步骤
        TestStep testStep = TestStep.builder()
                .testCaseId(testCaseId)
                .stepOrder(requestDTO.getStepOrder())
                .stepDescription(requestDTO.getStepDescription())
                .testData(requestDTO.getTestData())
                .expectedResult(requestDTO.getExpectedResult())
                .remark(requestDTO.getRemark())
                .isKeyStep(requestDTO.getIsKeyStep() != null ? requestDTO.getIsKeyStep() : false)
                .automated(requestDTO.getAutomated() != null ? requestDTO.getAutomated() : false)
                .automationCode(requestDTO.getAutomationCode())
                .estimatedTime(requestDTO.getEstimatedTime() != null ? requestDTO.getEstimatedTime() : 30)
                .enabled(true)
                .build();
        
        testStep = testStepRepository.save(testStep);
        log.info("测试步骤创建成功: id={}, stepOrder={}", testStep.getId(), testStep.getStepOrder());
        return testStep;
    }
    
    /**
     * 根据测试用例ID获取所有步骤
     */
    @Transactional(readOnly = true)
    public List<TestStep> getTestStepsByTestCaseId(Long testCaseId) {
        log.debug("获取测试步骤列表: testCaseId={}", testCaseId);
        return testStepRepository.findByTestCaseIdAndEnabledTrueOrderByStepOrderAsc(testCaseId);
    }
    
    /**
     * 根据ID获取测试步骤
     */
    @Transactional(readOnly = true)
    public TestStep getTestStepById(Long id) {
        return testStepRepository.findById(id)
                .filter(TestStep::getEnabled)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, "测试步骤不存在"));
    }
    
    /**
     * 更新测试步骤
     */
    public TestStep updateTestStep(Long id, CreateTestStepRequestDTO requestDTO) {
        log.info("更新测试步骤: id={}, stepOrder={}", id, requestDTO.getStepOrder());
        
        TestStep testStep = getTestStepById(id);
        
        // 检查步骤序号是否重复（排除自己）
        if (!requestDTO.getStepOrder().equals(testStep.getStepOrder()) &&
            testStepRepository.existsByTestCaseIdAndStepOrderAndEnabledTrueAndIdNot(
                testStep.getTestCaseId(), requestDTO.getStepOrder(), id)) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "步骤序号已存在: " + requestDTO.getStepOrder());
        }
        
        // 更新步骤信息
        testStep.setStepOrder(requestDTO.getStepOrder());
        testStep.setStepDescription(requestDTO.getStepDescription());
        testStep.setTestData(requestDTO.getTestData());
        testStep.setExpectedResult(requestDTO.getExpectedResult());
        testStep.setRemark(requestDTO.getRemark());
        testStep.setIsKeyStep(requestDTO.getIsKeyStep() != null ? requestDTO.getIsKeyStep() : testStep.getIsKeyStep());
        testStep.setAutomated(requestDTO.getAutomated() != null ? requestDTO.getAutomated() : testStep.getAutomated());
        testStep.setAutomationCode(requestDTO.getAutomationCode());
        testStep.setEstimatedTime(requestDTO.getEstimatedTime() != null ? requestDTO.getEstimatedTime() : testStep.getEstimatedTime());
        
        testStep = testStepRepository.save(testStep);
        log.info("测试步骤更新成功: id={}", testStep.getId());
        return testStep;
    }
    
    /**
     * 删除测试步骤
     */
    public void deleteTestStep(Long id) {
        log.info("删除测试步骤: id={}", id);
        
        TestStep testStep = getTestStepById(id);
        
        // 软删除
        testStep.setEnabled(false);
        testStepRepository.save(testStep);
        
        log.info("测试步骤删除成功: id={}", id);
    }
    
    /**
     * 调整步骤顺序
     */
    public void reorderTestStep(Long stepId, Integer newOrder) {
        log.info("调整步骤顺序: stepId={}, newOrder={}", stepId, newOrder);
        
        TestStep testStep = getTestStepById(stepId);
        
        // 检查新序号是否重复
        if (testStepRepository.existsByTestCaseIdAndStepOrderAndEnabledTrueAndIdNot(
                testStep.getTestCaseId(), newOrder, stepId)) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "目标位置已存在步骤");
        }
        
        Integer oldOrder = testStep.getStepOrder();
        testStep.setStepOrder(newOrder);
        testStepRepository.save(testStep);
        
        // 调整其他步骤的序号
        adjustStepOrders(testStep.getTestCaseId(), oldOrder, newOrder);
        
        log.info("步骤顺序调整成功: stepId={}", stepId);
    }
    
    /**
     * 获取关键步骤
     */
    @Transactional(readOnly = true)
    public List<TestStep> getKeyStepsByTestCaseId(Long testCaseId) {
        log.debug("获取关键步骤: testCaseId={}", testCaseId);
        return testStepRepository.findByTestCaseIdAndIsKeyStepTrueAndEnabledTrueOrderByStepOrderAsc(testCaseId);
    }
    
    /**
     * 获取自动化步骤
     */
    @Transactional(readOnly = true)
    public List<TestStep> getAutomatedStepsByTestCaseId(Long testCaseId) {
        log.debug("获取自动化步骤: testCaseId={}", testCaseId);
        return testStepRepository.findByTestCaseIdAndAutomatedTrueAndEnabledTrueOrderByStepOrderAsc(testCaseId);
    }
    
    /**
     * 批量创建测试步骤
     */
    public List<TestStep> batchCreateTestSteps(List<CreateTestStepRequestDTO> requestDTOs, Long testCaseId) {
        log.info("批量创建测试步骤: testCaseId={}, count={}", testCaseId, requestDTOs.size());
        
        List<TestStep> testSteps = new ArrayList<>();
        
        for (CreateTestStepRequestDTO requestDTO : requestDTOs) {
            // 检查步骤序号是否重复
            if (testStepRepository.existsByTestCaseIdAndStepOrderAndEnabledTrueAndIdNot(
                    testCaseId, requestDTO.getStepOrder(), -1L)) {
                throw new BusinessException(ErrorCode.DATA_EXISTS, "步骤序号已存在: " + requestDTO.getStepOrder());
            }
            
            TestStep testStep = TestStep.builder()
                    .testCaseId(testCaseId)
                    .stepOrder(requestDTO.getStepOrder())
                    .stepDescription(requestDTO.getStepDescription())
                    .testData(requestDTO.getTestData())
                    .expectedResult(requestDTO.getExpectedResult())
                    .remark(requestDTO.getRemark())
                    .isKeyStep(requestDTO.getIsKeyStep() != null ? requestDTO.getIsKeyStep() : false)
                    .automated(requestDTO.getAutomated() != null ? requestDTO.getAutomated() : false)
                    .automationCode(requestDTO.getAutomationCode())
                    .estimatedTime(requestDTO.getEstimatedTime() != null ? requestDTO.getEstimatedTime() : 30)
                    .enabled(true)
                    .build();
            
            testSteps.add(testStep);
        }
        
        testSteps = testStepRepository.saveAll(testSteps);
        log.info("批量创建测试步骤成功: count={}", testSteps.size());
        return testSteps;
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 调整其他步骤的序号
     */
    private void adjustStepOrders(Long testCaseId, Integer oldOrder, Integer newOrder) {
        if (oldOrder.equals(newOrder)) {
            return;
        }
        
        List<TestStep> stepsToAdjust;
        
        if (oldOrder < newOrder) {
            // 向后移动，前面的步骤序号减1
            stepsToAdjust = testStepRepository.findByTestCaseIdAndStepOrderGreaterThanAndEnabledTrueOrderByStepOrderAsc(
                    testCaseId, oldOrder);
            for (TestStep step : stepsToAdjust) {
                if (step.getStepOrder() <= newOrder) {
                    step.setStepOrder(step.getStepOrder() - 1);
                    testStepRepository.save(step);
                }
            }
        } else {
            // 向前移动，后面的步骤序号加1
            stepsToAdjust = testStepRepository.findByTestCaseIdAndStepOrderLessThanAndEnabledTrueOrderByStepOrderAsc(
                    testCaseId, oldOrder);
            for (TestStep step : stepsToAdjust) {
                if (step.getStepOrder() >= newOrder) {
                    step.setStepOrder(step.getStepOrder() + 1);
                    testStepRepository.save(step);
                }
            }
        }
    }
}