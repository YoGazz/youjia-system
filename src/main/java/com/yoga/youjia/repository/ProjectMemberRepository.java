package com.yoga.youjia.repository;

import com.yoga.youjia.common.enums.ProjectMemberRole;
import com.yoga.youjia.entity.ProjectMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 项目成员数据访问层
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    
    /**
     * 根据项目ID和用户ID查找成员
     */
    Optional<ProjectMember> findByProject_IdAndUserId(Long projectId, Long userId);
    
    /**
     * 检查项目成员是否存在
     */
    boolean existsByProject_IdAndUserId(Long projectId, Long userId);
    
    /**
     * 根据项目ID查找所有成员
     */
    List<ProjectMember> findByProject_Id(Long projectId);
    
    /**
     * 根据项目ID查找活跃成员
     */
    List<ProjectMember> findByProject_IdAndActiveTrue(Long projectId);
    
    /**
     * 根据用户ID查找所有项目成员记录
     */
    List<ProjectMember> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找活跃的项目成员记录
     */
    List<ProjectMember> findByUserIdAndActiveTrue(Long userId);
    
    /**
     * 根据项目ID和角色查找成员
     */
    List<ProjectMember> findByProject_IdAndRole(Long projectId, ProjectMemberRole role);
    
    /**
     * 根据项目ID和角色查找活跃成员
     */
    List<ProjectMember> findByProject_IdAndRoleAndActiveTrue(Long projectId, ProjectMemberRole role);
    
    /**
     * 分页查询项目成员
     */
    Page<ProjectMember> findByProject_Id(Long projectId, Pageable pageable);
    
    /**
     * 分页查询活跃项目成员
     */
    Page<ProjectMember> findByProject_IdAndActiveTrue(Long projectId, Pageable pageable);
    
    /**
     * 统计项目成员数量
     */
    long countByProject_Id(Long projectId);
    
    /**
     * 统计活跃项目成员数量
     */
    long countByProject_IdAndActiveTrue(Long projectId);
    
    /**
     * 统计用户参与的项目数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户参与的活跃项目数量
     */
    long countByUserIdAndActiveTrue(Long userId);
    
    /**
     * 查找项目的管理员成员
     */
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId " +
           "AND pm.active = true AND pm.role IN ('PROJECT_MANAGER', 'TEST_MANAGER')")
    List<ProjectMember> findManagersByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 查找项目的项目经理
     */
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId " +
           "AND pm.active = true AND pm.role = 'PROJECT_MANAGER'")
    List<ProjectMember> findProjectManagersByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 检查用户是否为项目管理员
     */
    @Query("SELECT COUNT(pm) > 0 FROM ProjectMember pm WHERE pm.project.id = :projectId " +
           "AND pm.userId = :userId AND pm.active = true " +
           "AND pm.role IN ('PROJECT_MANAGER', 'TEST_MANAGER')")
    boolean isProjectManager(@Param("projectId") Long projectId, @Param("userId") Long userId);
    
    /**
     * 检查用户是否为项目经理
     */
    @Query("SELECT COUNT(pm) > 0 FROM ProjectMember pm WHERE pm.project.id = :projectId " +
           "AND pm.userId = :userId AND pm.active = true AND pm.role = 'PROJECT_MANAGER'")
    boolean isProjectOwner(@Param("projectId") Long projectId, @Param("userId") Long userId);
    
    /**
     * 根据用户ID和角色查找项目成员
     */
    List<ProjectMember> findByUserIdAndRole(Long userId, ProjectMemberRole role);
    
    /**
     * 根据用户ID和角色查找活跃项目成员
     */
    List<ProjectMember> findByUserIdAndRoleAndActiveTrue(Long userId, ProjectMemberRole role);
    
    /**
     * 查找项目中指定用户的成员信息（包含项目信息）
     */
    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.project p " +
           "WHERE pm.userId = :userId AND pm.active = true AND p.enabled = true")
    List<ProjectMember> findActiveProjectMembersWithProject(@Param("userId") Long userId);
    
    /**
     * 分页查询项目中指定用户的成员信息
     */
    @Query(value = "SELECT pm FROM ProjectMember pm JOIN FETCH pm.project p " +
           "WHERE pm.userId = :userId AND pm.active = true AND p.enabled = true",
           countQuery = "SELECT COUNT(pm) FROM ProjectMember pm JOIN pm.project p " +
           "WHERE pm.userId = :userId AND pm.active = true AND p.enabled = true")
    Page<ProjectMember> findActiveProjectMembersWithProject(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计项目各角色的成员数量
     */
    @Query("SELECT pm.role, COUNT(pm) FROM ProjectMember pm " +
           "WHERE pm.project.id = :projectId AND pm.active = true GROUP BY pm.role")
    List<Object[]> countMembersByRoleInProject(@Param("projectId") Long projectId);
    
    /**
     * 查找用户在多个项目中的角色
     */
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.userId = :userId " +
           "AND pm.project.id IN :projectIds AND pm.active = true")
    List<ProjectMember> findUserRolesInProjects(@Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);
    
    /**
     * 删除项目的所有成员
     */
    void deleteByProject_Id(Long projectId);
    
    /**
     * 根据用户ID删除所有项目成员记录
     */
    void deleteByUserId(Long userId);
    
    /**
     * 查找最近加入的项目成员
     */
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId " +
           "AND pm.active = true ORDER BY pm.joinedAt DESC")
    List<ProjectMember> findRecentMembersByProjectId(@Param("projectId") Long projectId, Pageable pageable);
}