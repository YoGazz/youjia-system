package com.yoga.youjia.service;

import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.enums.ProjectMemberRole;
import com.yoga.youjia.common.enums.ProjectStatus;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目管理服务类
 * 
 * 提供项目的创建、查询、更新、删除等业务功能
 */
@Service
public class ProjectService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    // ========== 项目基本操作 ==========
    
    /**
     * 创建项目
     */
    @Transactional
    public Project createProject(Project project, Long creatorId) {
        logger.info("创建项目: code={}, name={}, creator={}", project.getCode(), project.getName(), creatorId);
        
        // 检查项目编码是否已存在
        if (projectRepository.existsByCode(project.getCode())) {
            throw new DataConflictException(ErrorCode.PROJECT_CODE_ALREADY_EXISTS, project.getCode());
        }
        
        // 设置创建者
        project.setCreatedBy(creatorId);
        project.setUpdatedBy(creatorId);
        
        // 保存项目
        Project savedProject = projectRepository.save(project);
        
        // 将创建者添加为项目经理
        ProjectMember creator = ProjectMember.builder()
                .project(savedProject)
                .userId(creatorId)
                .role(ProjectMemberRole.PROJECT_MANAGER)
                .addedBy(creatorId)
                .active(true)
                .build();
        
        projectMemberRepository.save(creator);
        
        logger.info("项目创建成功: id={}, code={}", savedProject.getId(), savedProject.getCode());
        return savedProject;
    }
    
    /**
     * 根据ID查询项目
     */
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND, "项目不存在: " + projectId));
    }
    
    /**
     * 根据编码查询项目
     */
    public Project getProjectByCode(String code) {
        return projectRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND, "项目不存在: " + code));
    }
    
    /**
     * 更新项目基本信息
     */
    @Transactional
    public Project updateProject(Long projectId, Project updateData, Long operatorId) {
        logger.info("更新项目: id={}, operator={}", projectId, operatorId);
        
        Project existingProject = getProjectById(projectId);
        
        // 检查操作权限
        checkProjectEditPermission(projectId, operatorId);
        
        // 检查项目编码冲突（如果编码有变更）
        if (!existingProject.getCode().equals(updateData.getCode()) &&
            projectRepository.existsByCode(updateData.getCode())) {
            throw new DataConflictException(ErrorCode.PROJECT_CODE_ALREADY_EXISTS, updateData.getCode());
        }
        
        // 更新字段
        existingProject.setCode(updateData.getCode());
        existingProject.setName(updateData.getName());
        existingProject.setDescription(updateData.getDescription());
        existingProject.setPriority(updateData.getPriority());
        existingProject.setStartDate(updateData.getStartDate());
        existingProject.setEndDate(updateData.getEndDate());
        existingProject.setVersion(updateData.getVersion());
        existingProject.setTags(updateData.getTags());
        existingProject.setSettings(updateData.getSettings());
        existingProject.setUpdatedBy(operatorId);
        
        Project savedProject = projectRepository.save(existingProject);
        logger.info("项目更新成功: id={}", savedProject.getId());
        return savedProject;
    }
    
    /**
     * 更新项目状态
     */
    @Transactional
    public Project updateProjectStatus(Long projectId, ProjectStatus newStatus, Long operatorId) {
        logger.info("更新项目状态: id={}, status={}, operator={}", projectId, newStatus, operatorId);
        
        Project project = getProjectById(projectId);
        
        // 检查操作权限（只有管理员可以更改状态）
        checkProjectManagePermission(projectId, operatorId);
        
        // 执行状态转换
        project.changeStatus(newStatus, operatorId);
        
        Project savedProject = projectRepository.save(project);
        logger.info("项目状态更新成功: id={}, status={}", savedProject.getId(), savedProject.getStatus());
        return savedProject;
    }
    
    /**
     * 启用/禁用项目
     */
    @Transactional
    public Project toggleProjectEnabled(Long projectId, Boolean enabled, Long operatorId) {
        logger.info("{}项目: id={}, operator={}", enabled ? "启用" : "禁用", projectId, operatorId);
        
        Project project = getProjectById(projectId);
        
        // 检查操作权限
        checkProjectManagePermission(projectId, operatorId);
        
        project.setEnabled(enabled);
        project.setUpdatedBy(operatorId);
        
        Project savedProject = projectRepository.save(project);
        logger.info("项目{}成功: id={}", enabled ? "启用" : "禁用", savedProject.getId());
        return savedProject;
    }
    
    /**
     * 删除项目（逻辑删除）
     */
    @Transactional
    public void deleteProject(Long projectId, Long operatorId) {
        logger.info("删除项目: id={}, operator={}", projectId, operatorId);
        
        Project project = getProjectById(projectId);
        
        // 检查操作权限
        checkProjectManagePermission(projectId, operatorId);
        
        // 检查项目状态（只有特定状态才能删除）
        if (project.getStatus() == ProjectStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.PROJECT_CANNOT_DELETE, "进行中的项目不能删除，请先变更项目状态");
        }
        
        // 逻辑删除（禁用项目）
        project.setEnabled(false);
        project.setUpdatedBy(operatorId);
        
        projectRepository.save(project);
        logger.info("项目删除成功: id={}", projectId);
    }
    
    // ========== 项目查询操作 ==========
    
    /**
     * 分页查询所有项目
     */
    public Page<Project> getProjects(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findByEnabledTrue(pageable);
    }
    
    /**
     * 根据用户ID分页查询用户参与的项目
     */
    public Page<Project> getProjectsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return projectRepository.findProjectsByMemberId(userId, pageable);
    }
    
    /**
     * 复合条件查询项目
     */
    public Page<Project> searchProjects(ProjectStatus status, Long createdBy, String keyword,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return projectRepository.findProjectsWithConditions(
            status, createdBy, keyword, startDate, endDate, pageable);
    }
    
    /**
     * 搜索项目（根据编码或名称）
     */
    public List<Project> searchProjectsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return projectRepository.findByEnabledTrue();
        }
        return projectRepository.findByCodeOrNameContaining(keyword.trim());
    }
    
    /**
     * 查找即将到期的项目
     */
    public List<Project> getProjectsNearDeadline(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(days);
        return projectRepository.findProjectsNearDeadline(now, deadline);
    }
    
    /**
     * 查找逾期的项目
     */
    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDateTime.now());
    }
    
    /**
     * 获取项目统计信息
     */
    public Map<String, Object> getProjectStatistics() {
        List<Object[]> statusCounts = projectRepository.countProjectsByStatus();
        Map<String, Long> statusMap = statusCounts.stream()
                .collect(Collectors.toMap(
                    arr -> ((ProjectStatus) arr[0]).getDisplayName(),
                    arr -> (Long) arr[1]
                ));
        
        return Map.of(
            "statusCounts", statusMap,
            "totalProjects", projectRepository.countByEnabledTrue(),
            "overdueProjects", getOverdueProjects().size(),
            "nearDeadlineProjects", getProjectsNearDeadline(7).size()
        );
    }
    
    /**
     * 获取用户项目统计信息
     */
    public Map<String, Object> getUserProjectStatistics(Long userId) {
        List<Object[]> statusCounts = projectRepository.countProjectsByStatusAndMemberId(userId);
        Map<String, Long> statusMap = statusCounts.stream()
                .collect(Collectors.toMap(
                    arr -> ((ProjectStatus) arr[0]).getDisplayName(),
                    arr -> (Long) arr[1]
                ));
        
        return Map.of(
            "statusCounts", statusMap,
            "totalProjects", projectRepository.countProjectsByMemberId(userId),
            "managedProjects", projectMemberRepository.countByUserIdAndActiveTrue(userId)
        );
    }
    
    // ========== 权限检查方法 ==========
    
    /**
     * 检查项目查看权限
     */
    public boolean checkProjectViewPermission(Long projectId, Long userId) {
        return projectMemberRepository.existsByProject_IdAndUserId(projectId, userId);
    }
    
    /**
     * 检查项目编辑权限
     */
    public void checkProjectEditPermission(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProject_IdAndUserId(projectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED, "您不是该项目的成员，无法执行此操作"));
        
        if (!member.canEdit()) {
            throw new BusinessException(ErrorCode.PROJECT_PERMISSION_DENIED, "您没有编辑该项目的权限");
        }
    }
    
    /**
     * 检查项目管理权限
     */
    public void checkProjectManagePermission(Long projectId, Long userId) {
        if (!projectMemberRepository.isProjectManager(projectId, userId)) {
            throw new BusinessException(ErrorCode.PROJECT_PERMISSION_DENIED, "您没有管理该项目的权限");
        }
    }
    
    /**
     * 检查项目所有者权限
     */
    public void checkProjectOwnerPermission(Long projectId, Long userId) {
        if (!projectMemberRepository.isProjectOwner(projectId, userId)) {
            throw new BusinessException(ErrorCode.PROJECT_PERMISSION_DENIED, "只有项目经理可以执行此操作");
        }
    }
}