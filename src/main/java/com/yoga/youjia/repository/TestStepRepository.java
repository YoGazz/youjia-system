package com.yoga.youjia.repository;

import com.yoga.youjia.entity.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 测试步骤数据访问层接口
 */
public interface TestStepRepository extends JpaRepository<TestStep, Long> {
    
    /**
     * 根据测试用例ID查找所有步骤
     */
    List<TestStep> findByTestCaseIdAndEnabledTrueOrderByStepOrderAsc(Long testCaseId);
    
    /**
     * 根据测试用例ID获取步骤数量
     */
    long countByTestCaseIdAndEnabledTrue(Long testCaseId);
    
    /**
     * 根据测试用例ID获取关键步骤
     */
    List<TestStep> findByTestCaseIdAndIsKeyStepTrueAndEnabledTrueOrderByStepOrderAsc(Long testCaseId);
    
    /**
     * 根据测试用例ID获取自动化步骤
     */
    List<TestStep> findByTestCaseIdAndAutomatedTrueAndEnabledTrueOrderByStepOrderAsc(Long testCaseId);
    
    /**
     * 获取指定用例下的最大步骤序号
     */
    @Query("SELECT COALESCE(MAX(ts.stepOrder), 0) FROM TestStep ts WHERE " +
           "ts.testCaseId = :testCaseId AND ts.enabled = true")
    Integer findMaxStepOrderByTestCase(@Param("testCaseId") Long testCaseId);
    
    /**
     * 根据测试用例ID删除所有步骤（软删除）
     */
    @Query("UPDATE TestStep ts SET ts.enabled = false WHERE ts.testCaseId = :testCaseId")
    void softDeleteByTestCaseId(@Param("testCaseId") Long testCaseId);
    
    /**
     * 批量更新步骤序号
     */
    @Query("UPDATE TestStep ts SET ts.stepOrder = :newOrder WHERE ts.id = :stepId")
    void updateStepOrder(@Param("stepId") Long stepId, @Param("newOrder") Integer newOrder);
    
    /**
     * 检查指定步骤序号是否已存在
     */
    boolean existsByTestCaseIdAndStepOrderAndEnabledTrueAndIdNot(
            Long testCaseId, Integer stepOrder, Long excludeId);
    
    /**
     * 获取指定序号之后的所有步骤
     */
    List<TestStep> findByTestCaseIdAndStepOrderGreaterThanAndEnabledTrueOrderByStepOrderAsc(
            Long testCaseId, Integer stepOrder);
    
    /**
     * 获取指定序号之前的所有步骤
     */
    List<TestStep> findByTestCaseIdAndStepOrderLessThanAndEnabledTrueOrderByStepOrderAsc(
            Long testCaseId, Integer stepOrder);
    
    /**
     * 批量删除测试用例的步骤
     */
    void deleteByTestCaseIdIn(List<Long> testCaseIds);
    
    /**
     * 统计所有测试用例的步骤总数
     */
    @Query("SELECT COUNT(ts) FROM TestStep ts JOIN ts.testCase tc WHERE " +
           "tc.projectId = :projectId AND ts.enabled = true AND tc.enabled = true")
    Long countStepsByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 统计自动化步骤数量
     */
    @Query("SELECT COUNT(ts) FROM TestStep ts JOIN ts.testCase tc WHERE " +
           "tc.projectId = :projectId AND ts.enabled = true AND tc.enabled = true AND ts.automated = true")
    Long countAutomatedStepsByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 统计关键步骤数量
     */
    @Query("SELECT COUNT(ts) FROM TestStep ts JOIN ts.testCase tc WHERE " +
           "tc.projectId = :projectId AND ts.enabled = true AND tc.enabled = true AND ts.isKeyStep = true")
    Long countKeyStepsByProjectId(@Param("projectId") Long projectId);
}