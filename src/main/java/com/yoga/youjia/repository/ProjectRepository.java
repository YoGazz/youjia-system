package com.yoga.youjia.repository;

import com.yoga.youjia.common.enums.ProjectStatus;
import com.yoga.youjia.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 项目数据访问层
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    /**
     * 根据项目编码查找项目
     */
    Optional<Project> findByCode(String code);
    
    /**
     * 检查项目编码是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 检查项目名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 统计启用的项目数量
     */
    long countByEnabledTrue();
    
    /**
     * 根据状态查找项目
     */
    List<Project> findByStatus(ProjectStatus status);
    
    /**
     * 根据创建者查找项目
     */
    List<Project> findByCreatedBy(Long createdBy);
    
    /**
     * 根据创建者和状态查找项目
     */
    List<Project> findByCreatedByAndStatus(Long createdBy, ProjectStatus status);
    
    /**
     * 查找启用状态的项目
     */
    List<Project> findByEnabledTrue();
    
    /**
     * 根据状态查找启用的项目
     */
    List<Project> findByEnabledTrueAndStatus(ProjectStatus status);
    
    /**
     * 分页查询启用的项目
     */
    Page<Project> findByEnabledTrue(Pageable pageable);
    
    /**
     * 根据项目名称模糊查询
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true AND p.name LIKE %:name%")
    List<Project> findByNameContaining(@Param("name") String name);
    
    /**
     * 根据项目编码或名称模糊查询
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true AND (p.code LIKE %:keyword% OR p.name LIKE %:keyword%)")
    List<Project> findByCodeOrNameContaining(@Param("keyword") String keyword);
    
    /**
     * 查找即将到期的项目（指定天数内）
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true AND p.status IN ('PLANNING', 'ACTIVE') " +
           "AND p.endDate IS NOT NULL AND p.endDate BETWEEN :now AND :deadline")
    List<Project> findProjectsNearDeadline(@Param("now") LocalDateTime now, @Param("deadline") LocalDateTime deadline);
    
    /**
     * 查找逾期的项目
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true AND p.status IN ('PLANNING', 'ACTIVE') " +
           "AND p.endDate IS NOT NULL AND p.endDate < :now")
    List<Project> findOverdueProjects(@Param("now") LocalDateTime now);
    
    /**
     * 根据用户ID查找用户参与的项目
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members m WHERE p.enabled = true " +
           "AND m.userId = :userId AND m.active = true")
    List<Project> findProjectsByMemberId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和项目状态查找用户参与的项目
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members m WHERE p.enabled = true " +
           "AND m.userId = :userId AND m.active = true AND p.status = :status")
    List<Project> findProjectsByMemberIdAndStatus(@Param("userId") Long userId, @Param("status") ProjectStatus status);
    
    /**
     * 分页查询用户参与的项目
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members m WHERE p.enabled = true " +
           "AND m.userId = :userId AND m.active = true")
    Page<Project> findProjectsByMemberId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计用户参与的项目数量
     */
    @Query("SELECT COUNT(DISTINCT p) FROM Project p JOIN p.members m WHERE p.enabled = true " +
           "AND m.userId = :userId AND m.active = true")
    long countProjectsByMemberId(@Param("userId") Long userId);
    
    /**
     * 根据标签查找项目
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true AND p.tags LIKE %:tag%")
    List<Project> findByTagsContaining(@Param("tag") String tag);
    
    /**
     * 统计各状态的项目数量
     */
    @Query("SELECT p.status, COUNT(p) FROM Project p WHERE p.enabled = true GROUP BY p.status")
    List<Object[]> countProjectsByStatus();
    
    /**
     * 统计指定用户各状态的项目数量
     */
    @Query("SELECT p.status, COUNT(DISTINCT p) FROM Project p JOIN p.members m " +
           "WHERE p.enabled = true AND m.userId = :userId AND m.active = true GROUP BY p.status")
    List<Object[]> countProjectsByStatusAndMemberId(@Param("userId") Long userId);
    
    /**
     * 查找最近创建的项目
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true ORDER BY p.createdAt DESC")
    List<Project> findRecentProjects(Pageable pageable);
    
    /**
     * 查找最近更新的项目
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true ORDER BY p.updatedAt DESC")
    List<Project> findRecentlyUpdatedProjects(Pageable pageable);
    
    /**
     * 复合条件查询项目
     */
    @Query("SELECT p FROM Project p WHERE p.enabled = true " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:createdBy IS NULL OR p.createdBy = :createdBy) " +
           "AND (:keyword IS NULL OR p.code LIKE %:keyword% OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) " +
           "AND (:startDate IS NULL OR p.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.endDate <= :endDate)")
    Page<Project> findProjectsWithConditions(
        @Param("status") ProjectStatus status,
        @Param("createdBy") Long createdBy,
        @Param("keyword") String keyword,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}