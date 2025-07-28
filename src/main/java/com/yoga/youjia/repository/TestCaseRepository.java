package com.yoga.youjia.repository;

import com.yoga.youjia.common.enums.TestCasePriority;
import com.yoga.youjia.common.enums.TestCaseStatus;
import com.yoga.youjia.common.enums.TestCaseType;
import com.yoga.youjia.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 测试用例数据访问层接口
 */
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    
    /**
     * 根据用例编号查找测试用例
     */
    Optional<TestCase> findByCaseIdAndEnabledTrue(String caseId);
    
    /**
     * 根据项目ID查找所有测试用例
     */
    Page<TestCase> findByProjectIdAndEnabledTrueOrderBySortOrderAscIdAsc(Long projectId, Pageable pageable);
    
    /**
     * 根据模块ID查找测试用例
     */
    Page<TestCase> findByModuleIdAndEnabledTrueOrderBySortOrderAscIdAsc(Long moduleId, Pageable pageable);
    
    /**
     * 根据项目ID和状态查找测试用例
     */
    Page<TestCase> findByProjectIdAndStatusAndEnabledTrueOrderBySortOrderAscIdAsc(
            Long projectId, TestCaseStatus status, Pageable pageable);
    
    /**
     * 检查用例编号是否已存在
     */
    boolean existsByCaseIdAndEnabledTrue(String caseId);
    
    /**
     * 检查用例编号是否已存在（排除指定ID）
     */
    boolean existsByCaseIdAndEnabledTrueAndIdNot(String caseId, Long excludeId);
    
    /**
     * 根据条件分页查询测试用例
     */
    @Query("SELECT tc FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND " +
           "tc.enabled = true AND " +
           "(:title IS NULL OR tc.title LIKE %:title%) AND " +
           "(:moduleId IS NULL OR tc.moduleId = :moduleId) AND " +
           "(:type IS NULL OR tc.type = :type) AND " +
           "(:priority IS NULL OR tc.priority = :priority) AND " +
           "(:status IS NULL OR tc.status = :status) AND " +
           "(:automated IS NULL OR tc.automated = :automated) AND " +
           "(:createdBy IS NULL OR tc.createdBy = :createdBy) AND " +
           "(:tag IS NULL OR tc.tags LIKE %:tag%)")
    Page<TestCase> findByConditions(
            @Param("projectId") Long projectId,
            @Param("title") String title,
            @Param("moduleId") Long moduleId,
            @Param("type") TestCaseType type,
            @Param("priority") TestCasePriority priority,
            @Param("status") TestCaseStatus status,
            @Param("automated") Boolean automated,
            @Param("createdBy") Long createdBy,
            @Param("tag") String tag,
            Pageable pageable);
    
    /**
     * 根据需求ID查找测试用例
     */
    List<TestCase> findByRequirementIdAndEnabledTrueOrderBySortOrderAscIdAsc(Long requirementId);
    
    /**
     * 根据标签查找测试用例
     */
    @Query("SELECT tc FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND " +
           "tc.enabled = true AND " +
           "tc.tags LIKE %:tag% " +
           "ORDER BY tc.sortOrder, tc.id")
    List<TestCase> findByProjectIdAndTag(@Param("projectId") Long projectId, @Param("tag") String tag);
    
    /**
     * 统计项目下各状态的测试用例数量
     */
    @Query("SELECT tc.status, COUNT(tc) FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND tc.enabled = true " +
           "GROUP BY tc.status")
    List<Object[]> countByProjectIdAndStatusGroupByStatus(@Param("projectId") Long projectId);
    
    /**
     * 统计项目下各类型的测试用例数量
     */
    @Query("SELECT tc.type, COUNT(tc) FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND tc.enabled = true " +
           "GROUP BY tc.type")
    List<Object[]> countByProjectIdAndTypeGroupByType(@Param("projectId") Long projectId);
    
    /**
     * 统计项目下各优先级的测试用例数量
     */
    @Query("SELECT tc.priority, COUNT(tc) FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND tc.enabled = true " +
           "GROUP BY tc.priority")
    List<Object[]> countByProjectIdAndPriorityGroupByPriority(@Param("projectId") Long projectId);
    
    /**
     * 获取项目下的测试用例总数
     */
    long countByProjectIdAndEnabledTrue(Long projectId);
    
    /**
     * 获取模块下的测试用例总数
     */
    long countByModuleIdAndEnabledTrue(Long moduleId);
    
    /**
     * 获取自动化测试用例数量
     */
    long countByProjectIdAndAutomatedTrueAndEnabledTrue(Long projectId);
    
    /**
     * 获取指定时间范围内创建的测试用例
     */
    List<TestCase> findByProjectIdAndEnabledTrueAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long projectId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取待审核的测试用例
     */
    List<TestCase> findByProjectIdAndStatusAndEnabledTrueOrderByCreatedAtAsc(
            Long projectId, TestCaseStatus status);
    
    /**
     * 根据创建人查找测试用例
     */
    Page<TestCase> findByProjectIdAndCreatedByAndEnabledTrueOrderBySortOrderAscIdAsc(
            Long projectId, Long createdBy, Pageable pageable);
    
    /**
     * 根据项目ID删除所有测试用例（软删除）
     */
    @Query("UPDATE TestCase tc SET tc.enabled = false WHERE tc.projectId = :projectId")
    void softDeleteByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 根据模块ID删除所有测试用例（软删除）
     */
    @Query("UPDATE TestCase tc SET tc.enabled = false WHERE tc.moduleId = :moduleId")
    void softDeleteByModuleId(@Param("moduleId") Long moduleId);
    
    /**
     * 获取指定模块下的最大排序号
     */
    @Query("SELECT COALESCE(MAX(tc.sortOrder), 0) FROM TestCase tc WHERE " +
           "tc.moduleId = :moduleId AND tc.enabled = true")
    Integer findMaxSortOrderByModule(@Param("moduleId") Long moduleId);
    
    /**
     * 生成用例编号的下一个序号
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(tc.caseId, LENGTH(:prefix) + 1) AS INTEGER)), 0) + 1 " +
           "FROM TestCase tc WHERE tc.caseId LIKE CONCAT(:prefix, '%') AND tc.enabled = true")
    Integer generateNextCaseNumber(@Param("prefix") String prefix);
    
    /**
     * 批量更新测试用例状态
     */
    @Query("UPDATE TestCase tc SET tc.status = :status, tc.updatedBy = :updatedBy " +
           "WHERE tc.id IN :ids")
    void batchUpdateStatus(@Param("ids") List<Long> ids, 
                          @Param("status") TestCaseStatus status, 
                          @Param("updatedBy") Long updatedBy);
    
    /**
     * 批量更新测试用例模块
     */
    @Query("UPDATE TestCase tc SET tc.moduleId = :moduleId, tc.updatedBy = :updatedBy " +
           "WHERE tc.id IN :ids")
    void batchUpdateModule(@Param("ids") List<Long> ids, 
                          @Param("moduleId") Long moduleId, 
                          @Param("updatedBy") Long updatedBy);
    
    /**
     * 搜索测试用例（全文搜索）
     */
    @Query("SELECT tc FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND " +
           "tc.enabled = true AND " +
           "(tc.title LIKE %:keyword% OR " +
           "tc.description LIKE %:keyword% OR " +
           "tc.caseId LIKE %:keyword% OR " +
           "tc.tags LIKE %:keyword%)")
    Page<TestCase> searchByKeyword(@Param("projectId") Long projectId, 
                                  @Param("keyword") String keyword, 
                                  Pageable pageable);
    
    /**
     * 获取相似的测试用例（基于标题相似度）
     */
    @Query("SELECT tc FROM TestCase tc WHERE " +
           "tc.projectId = :projectId AND " +
           "tc.enabled = true AND " +
           "tc.id != :excludeId AND " +
           "tc.title LIKE %:titleKeyword% " +
           "ORDER BY tc.priority, tc.createdAt DESC")
    List<TestCase> findSimilarTestCases(@Param("projectId") Long projectId, 
                                       @Param("excludeId") Long excludeId, 
                                       @Param("titleKeyword") String titleKeyword);
}