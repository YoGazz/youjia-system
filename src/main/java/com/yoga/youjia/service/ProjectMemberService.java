package com.yoga.youjia.service;

import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.enums.ProjectMemberRole;
import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.common.exception.DataConflictException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import com.yoga.youjia.entity.Project;
import com.yoga.youjia.entity.ProjectMember;
import com.yoga.youjia.repository.ProjectMemberRepository;
import com.yoga.youjia.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目成员管理服务类
 * 
 * 提供项目成员的增删改查和权限管理功能
 */
@Service
public class ProjectMemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberService.class);
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ProjectService projectService;
    
    // ========== 成员基本操作 ==========
    
    /**
     * 添加项目成员
     */
    @Transactional
    public ProjectMember addProjectMember(Long projectId, Long userId, ProjectMemberRole role, 
                                         String remarks, Long operatorId) {
        logger.info("添加项目成员: projectId={}, userId={}, role={}, operator={}", 
                   projectId, userId, role, operatorId);
        
        // 检查项目是否存在
        Project project = projectService.getProjectById(projectId);
        
        // 检查操作权限
        projectService.checkProjectManagePermission(projectId, operatorId);
        
        // 检查成员是否已存在
        if (projectMemberRepository.existsByProject_IdAndUserId(projectId, userId)) {
            throw new DataConflictException(ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS, 
                String.format("用户已是项目成员: 项目ID=%d, 用户ID=%d", projectId, userId));
        }
        
        // 创建成员记录
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .role(role)
                .remarks(remarks)
                .addedBy(operatorId)
                .active(true)
                .build();
        
        ProjectMember savedMember = projectMemberRepository.save(member);
        logger.info("项目成员添加成功: id={}, projectId={}, userId={}", 
                   savedMember.getId(), projectId, userId);
        return savedMember;
    }
    
    /**
     * 移除项目成员
     */
    @Transactional
    public void removeProjectMember(Long projectId, Long userId, Long operatorId) {
        logger.info("移除项目成员: projectId={}, userId={}, operator={}", projectId, userId, operatorId);
        
        // 检查操作权限
        projectService.checkProjectManagePermission(projectId, operatorId);
        
        // 查找成员记录
        ProjectMember member = projectMemberRepository.findByProject_IdAndUserId(projectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_MEMBER_NOT_FOUND, 
                    String.format("项目成员不存在: 项目ID=%d, 用户ID=%d", projectId, userId)));
        
        // 检查是否为最后一个项目经理
        if (member.getRole() == ProjectMemberRole.PROJECT_MANAGER) {
            long managerCount = projectMemberRepository.findProjectManagersByProjectId(projectId).size();
            if (managerCount <= 1) {
                throw new BusinessException(ErrorCode.PROJECT_MANAGER_REQUIRED, "项目必须至少保留一个项目经理");
            }
        }
        
        // 不能移除自己（项目经理）
        if (userId.equals(operatorId)) {
            throw new BusinessException(ErrorCode.PROJECT_PERMISSION_DENIED, "不能移除自己，请先转移项目经理权限");
        }
        
        // 逻辑删除
        member.leaveProject();
        projectMemberRepository.save(member);
        
        logger.info("项目成员移除成功: projectId={}, userId={}", projectId, userId);
    }
    
    /**
     * 更新成员角色
     */
    @Transactional
    public ProjectMember updateMemberRole(Long projectId, Long userId, ProjectMemberRole newRole, Long operatorId) {
        logger.info("更新成员角色: projectId={}, userId={}, newRole={}, operator={}", 
                   projectId, userId, newRole, operatorId);
        
        // 检查操作权限
        projectService.checkProjectManagePermission(projectId, operatorId);
        
        // 查找成员记录
        ProjectMember member = projectMemberRepository.findByProject_IdAndUserId(projectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_MEMBER_NOT_FOUND, 
                    String.format("项目成员不存在: 项目ID=%d, 用户ID=%d", projectId, userId)));
        
        // 检查是否为最后一个项目经理
        if (member.getRole() == ProjectMemberRole.PROJECT_MANAGER && newRole != ProjectMemberRole.PROJECT_MANAGER) {
            long managerCount = projectMemberRepository.findProjectManagersByProjectId(projectId).size();
            if (managerCount <= 1) {
                throw new BusinessException(ErrorCode.PROJECT_MANAGER_REQUIRED, "项目必须至少保留一个项目经理");
            }
        }
        
        // 更新角色
        member.changeRole(newRole);
        ProjectMember savedMember = projectMemberRepository.save(member);
        
        logger.info("成员角色更新成功: id={}, newRole={}", savedMember.getId(), newRole);
        return savedMember;
    }
    
    /**
     * 更新成员备注
     */
    @Transactional
    public ProjectMember updateMemberRemarks(Long projectId, Long userId, String remarks, Long operatorId) {
        logger.info("更新成员备注: projectId={}, userId={}, operator={}", projectId, userId, operatorId);
        
        // 检查操作权限
        projectService.checkProjectManagePermission(projectId, operatorId);
        
        // 查找成员记录
        ProjectMember member = projectMemberRepository.findByProject_IdAndUserId(projectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_MEMBER_NOT_FOUND, 
                    String.format("项目成员不存在: 项目ID=%d, 用户ID=%d", projectId, userId)));
        
        member.setRemarks(remarks);
        ProjectMember savedMember = projectMemberRepository.save(member);
        
        logger.info("成员备注更新成功: id={}", savedMember.getId());
        return savedMember;
    }
    
    // ========== 成员查询操作 ==========
    
    /**
     * 获取项目所有成员
     */
    public List<ProjectMember> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByProject_IdAndActiveTrue(projectId);
    }
    
    /**
     * 分页查询项目成员
     */
    public Page<ProjectMember> getProjectMembers(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").descending());
        return projectMemberRepository.findByProject_IdAndActiveTrue(projectId, pageable);
    }
    
    /**
     * 根据角色查询项目成员
     */
    public List<ProjectMember> getProjectMembersByRole(Long projectId, ProjectMemberRole role) {
        return projectMemberRepository.findByProject_IdAndRoleAndActiveTrue(projectId, role);
    }
    
    /**
     * 获取项目管理员
     */
    public List<ProjectMember> getProjectManagers(Long projectId) {
        return projectMemberRepository.findManagersByProjectId(projectId);
    }
    
    /**
     * 获取项目经理
     */
    public List<ProjectMember> getProjectOwners(Long projectId) {
        return projectMemberRepository.findProjectManagersByProjectId(projectId);
    }
    
    /**
     * 获取用户参与的所有项目成员记录
     */
    public List<ProjectMember> getUserProjectMembers(Long userId) {
        return projectMemberRepository.findActiveProjectMembersWithProject(userId);
    }
    
    /**
     * 分页查询用户参与的项目成员记录
     */
    public Page<ProjectMember> getUserProjectMembers(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").descending());
        return projectMemberRepository.findActiveProjectMembersWithProject(userId, pageable);
    }
    
    /**
     * 获取用户在指定项目的成员信息
     */
    public ProjectMember getUserProjectMember(Long projectId, Long userId) {
        return projectMemberRepository.findByProject_IdAndUserId(projectId, userId)
                .orElse(null);
    }
    
    /**
     * 获取项目成员统计信息
     */
    public Map<String, Object> getProjectMemberStatistics(Long projectId) {
        List<Object[]> roleCounts = projectMemberRepository.countMembersByRoleInProject(projectId);
        Map<String, Long> roleMap = roleCounts.stream()
                .collect(Collectors.toMap(
                    arr -> ((ProjectMemberRole) arr[0]).getDisplayName(),
                    arr -> (Long) arr[1]
                ));
        
        return Map.of(
            "roleCounts", roleMap,
            "totalMembers", projectMemberRepository.countByProject_IdAndActiveTrue(projectId),
            "managerCount", projectMemberRepository.findManagersByProjectId(projectId).size()
        );
    }
    
    // ========== 权限检查方法 ==========
    
    /**
     * 检查用户是否为项目成员
     */
    public boolean isProjectMember(Long projectId, Long userId) {
        return projectMemberRepository.existsByProject_IdAndUserId(projectId, userId);
    }
    
    /**
     * 检查用户是否为项目管理员
     */
    public boolean isProjectManager(Long projectId, Long userId) {
        return projectMemberRepository.isProjectManager(projectId, userId);
    }
    
    /**
     * 检查用户是否为项目经理
     */
    public boolean isProjectOwner(Long projectId, Long userId) {
        return projectMemberRepository.isProjectOwner(projectId, userId);
    }
    
    /**
     * 获取用户在项目中的角色
     */
    public ProjectMemberRole getUserRoleInProject(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProject_IdAndUserId(projectId, userId)
                .orElse(null);
        return member != null && member.isActive() ? member.getRole() : null;
    }
    
    /**
     * 检查用户在项目中的权限
     */
    public Map<String, Boolean> getUserPermissionsInProject(Long projectId, Long userId) {
        ProjectMember member = getUserProjectMember(projectId, userId);
        
        if (member == null || !member.isActive()) {
            return Map.of(
                "canView", false,
                "canEdit", false,
                "canDelete", false,
                "canManageMembers", false,
                "canCreateTestCases", false,
                "canExecuteTests", false,
                "canManageBugs", false
            );
        }
        
        return Map.of(
            "canView", true,
            "canEdit", member.canEdit(),
            "canDelete", member.canDelete(),
            "canManageMembers", member.canManageMembers(),
            "canCreateTestCases", member.canCreateTestCases(),
            "canExecuteTests", member.canExecuteTests(),
            "canManageBugs", member.canManageBugs()
        );
    }
    
    // ========== 批量操作 ==========
    
    /**
     * 批量添加项目成员
     */
    @Transactional
    public List<ProjectMember> addProjectMembers(Long projectId, List<Long> userIds, 
                                               ProjectMemberRole role, Long operatorId) {
        logger.info("批量添加项目成员: projectId={}, userCount={}, role={}, operator={}", 
                   projectId, userIds.size(), role, operatorId);
        
        // 检查操作权限
        projectService.checkProjectManagePermission(projectId, operatorId);
        
        Project project = projectService.getProjectById(projectId);
        
        return userIds.stream()
                .filter(userId -> !projectMemberRepository.existsByProject_IdAndUserId(projectId, userId))
                .map(userId -> {
                    ProjectMember member = ProjectMember.builder()
                            .project(project)
                            .userId(userId)
                            .role(role)
                            .addedBy(operatorId)
                            .active(true)
                            .build();
                    return projectMemberRepository.save(member);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 批量移除项目成员
     */
    @Transactional
    public void removeProjectMembers(Long projectId, List<Long> userIds, Long operatorId) {
        logger.info("批量移除项目成员: projectId={}, userCount={}, operator={}", 
                   projectId, userIds.size(), operatorId);
        
        // 检查操作权限
        projectService.checkProjectManagePermission(projectId, operatorId);
        
        userIds.forEach(userId -> {
            if (!userId.equals(operatorId)) { // 不能移除自己
                try {
                    removeProjectMember(projectId, userId, operatorId);
                } catch (Exception e) {
                    logger.warn("移除项目成员失败: projectId={}, userId={}, error={}", 
                               projectId, userId, e.getMessage());
                }
            }
        });
    }
}