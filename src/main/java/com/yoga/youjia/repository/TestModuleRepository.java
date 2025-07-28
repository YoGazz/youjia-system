package com.yoga.youjia.repository;

import com.yoga.youjia.entity.TestModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 测试模块数据访问层接口
 */
public interface TestModuleRepository extends JpaRepository<TestModule, Long> {
    
    /**
     * 根据项目ID查找所有模块
     */
    List<TestModule> findByProjectIdAndEnabledTrueOrderBySortOrderAscIdAsc(Long projectId);
    
    /**
     * 根据项目ID和父模块ID查找子模块
     */
    List<TestModule> findByProjectIdAndParentIdAndEnabledTrueOrderBySortOrderAscIdAsc(Long projectId, Long parentId);
    
    /**
     * 根据项目ID查找根模块（父模块ID为空）
     */
    List<TestModule> findByProjectIdAndParentIdIsNullAndEnabledTrueOrderBySortOrderAscIdAsc(Long projectId);
    
    /**
     * 根据项目ID和模块名称查找模块
     */
    Optional<TestModule> findByProjectIdAndNameAndEnabledTrue(Long projectId, String name);
    
    /**
     * 根据项目ID和模块路径查找模块
     */
    Optional<TestModule> findByProjectIdAndModulePathAndEnabledTrue(Long projectId, String modulePath);
    
    /**
     * 检查模块名称在同一父模块下是否重复
     */
    boolean existsByProjectIdAndParentIdAndNameAndEnabledTrueAndIdNot(
            Long projectId, Long parentId, String name, Long excludeId);
    
    /**
     * 检查模块名称在同一父模块下是否重复（新建时）
     */
    boolean existsByProjectIdAndParentIdAndNameAndEnabledTrue(
            Long projectId, Long parentId, String name);
    
    /**
     * 根据条件分页查询模块
     */
    @Query("SELECT tm FROM TestModule tm WHERE " +
           "tm.projectId = :projectId AND " +
           "tm.enabled = true AND " +
           "(:name IS NULL OR tm.name LIKE %:name%) AND " +
           "(:parentId IS NULL OR tm.parentId = :parentId) AND " +
           "(:depth IS NULL OR tm.depth = :depth)")
    Page<TestModule> findByConditions(
            @Param("projectId") Long projectId,
            @Param("name") String name,
            @Param("parentId") Long parentId,
            @Param("depth") Integer depth,
            Pageable pageable);
    
    /**
     * 获取指定模块的所有子模块（递归查询）
     */
    @Query("SELECT tm FROM TestModule tm WHERE " +
           "tm.projectId = :projectId AND " +
           "tm.enabled = true AND " +
           "tm.modulePath LIKE CONCAT(:modulePath, '%') " +
           "ORDER BY tm.depth, tm.sortOrder, tm.id")
    List<TestModule> findAllChildrenByModulePath(@Param("projectId") Long projectId, @Param("modulePath") String modulePath);
    
    /**
     * 获取模块下测试用例总数（包含子模块）
     */
    @Query("SELECT COUNT(tc) FROM TestCase tc " +
           "JOIN tc.testModule tm WHERE " +
           "tm.projectId = :projectId AND " +
           "tm.enabled = true AND " +
           "(tm.id = :moduleId OR tm.modulePath LIKE CONCAT(:modulePath, '%'))")
    Long countTestCasesByModuleRecursive(@Param("projectId") Long projectId, 
                                        @Param("moduleId") Long moduleId, 
                                        @Param("modulePath") String modulePath);
    
    /**
     * 获取指定深度的所有模块
     */
    List<TestModule> findByProjectIdAndDepthAndEnabledTrueOrderBySortOrderAscIdAsc(Long projectId, Integer depth);
    
    /**
     * 根据项目ID删除所有模块（软删除）
     */
    @Query("UPDATE TestModule tm SET tm.enabled = false WHERE tm.projectId = :projectId")
    void softDeleteByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 获取指定父模块下的最大排序号
     */
    @Query("SELECT COALESCE(MAX(tm.sortOrder), 0) FROM TestModule tm WHERE " +
           "tm.projectId = :projectId AND tm.parentId = :parentId AND tm.enabled = true")
    Integer findMaxSortOrderByParent(@Param("projectId") Long projectId, @Param("parentId") Long parentId);
    
    /**
     * 获取项目下的模块总数
     */
    long countByProjectIdAndEnabledTrue(Long projectId);
    
    /**
     * 检查模块是否存在子模块
     */
    boolean existsByParentIdAndEnabledTrue(Long parentId);
    
    /**
     * 获取模块树形结构（用于前端展示）
     */
    @Query("SELECT tm FROM TestModule tm WHERE " +
           "tm.projectId = :projectId AND " +
           "tm.enabled = true " +
           "ORDER BY tm.depth, tm.modulePath, tm.sortOrder, tm.id")
    List<TestModule> findModuleTreeByProjectId(@Param("projectId") Long projectId);
}