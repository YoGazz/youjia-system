package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.enums.ProjectMemberRole;
import com.yoga.youjia.dto.request.AddProjectMemberRequestDTO;
import com.yoga.youjia.dto.response.PageResponseDTO;
import com.yoga.youjia.dto.response.ProjectMemberResponseDTO;
import com.yoga.youjia.entity.ProjectMember;
import com.yoga.youjia.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目成员管理控制器
 * 
 * 提供项目成员的增删改查、权限管理等RESTful API
 */
@Tag(name = "项目成员管理", description = "项目成员的添加、移除、角色管理等功能")
@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberController.class);
    
    @Autowired
    private ProjectMemberService projectMemberService;
    
    // ========== 成员基本操作 ==========
    
    /**
     * 添加项目成员
     */
    @Operation(summary = "添加项目成员", description = "向项目中添加新成员")
    @PostMapping
    public ApiResponse<ProjectMemberResponseDTO> addProjectMember(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Valid @RequestBody AddProjectMemberRequestDTO requestDTO) {
        logger.info("添加项目成员: projectId={}, userId={}, role={}", 
                   projectId, requestDTO.getUserId(), requestDTO.getRole());
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        ProjectMember member = projectMemberService.addProjectMember(
            projectId, requestDTO.getUserId(), requestDTO.getRole(), 
            requestDTO.getRemarks(), currentUserId);
        
        ProjectMemberResponseDTO responseDTO = ProjectMemberResponseDTO.from(member);
        
        logger.info("项目成员添加成功: memberId={}, projectId={}, userId={}", 
                   member.getId(), projectId, requestDTO.getUserId());
        return ApiResponse.success(responseDTO, "成员添加成功");
    }
    
    /**
     * 移除项目成员
     */
    @Operation(summary = "移除项目成员", description = "从项目中移除成员")
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> removeProjectMember(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        logger.info("移除项目成员: projectId={}, userId={}", projectId, userId);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        projectMemberService.removeProjectMember(projectId, userId, currentUserId);
        
        logger.info("项目成员移除成功: projectId={}, userId={}", projectId, userId);
        return ApiResponse.success(null, "成员移除成功");
    }
    
    /**
     * 更新成员角色
     */
    @Operation(summary = "更新成员角色", description = "更新项目成员的角色")
    @PatchMapping("/{userId}/role")
    public ApiResponse<ProjectMemberResponseDTO> updateMemberRole(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "新角色") @RequestParam ProjectMemberRole role) {
        logger.info("更新成员角色: projectId={}, userId={}, newRole={}", projectId, userId, role);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        ProjectMember member = projectMemberService.updateMemberRole(projectId, userId, role, currentUserId);
        ProjectMemberResponseDTO responseDTO = ProjectMemberResponseDTO.from(member);
        
        logger.info("成员角色更新成功: projectId={}, userId={}, newRole={}", projectId, userId, role);
        return ApiResponse.success(responseDTO, "角色更新成功");
    }
    
    /**
     * 更新成员备注
     */
    @Operation(summary = "更新成员备注", description = "更新项目成员的备注信息")
    @PatchMapping("/{userId}/remarks")
    public ApiResponse<ProjectMemberResponseDTO> updateMemberRemarks(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "备注信息") @RequestParam String remarks) {
        logger.info("更新成员备注: projectId={}, userId={}", projectId, userId);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        ProjectMember member = projectMemberService.updateMemberRemarks(projectId, userId, remarks, currentUserId);
        ProjectMemberResponseDTO responseDTO = ProjectMemberResponseDTO.from(member);
        
        logger.info("成员备注更新成功: projectId={}, userId={}", projectId, userId);
        return ApiResponse.success(responseDTO, "备注更新成功");
    }
    
    // ========== 成员查询操作 ==========
    
    /**
     * 获取项目所有成员
     */
    @Operation(summary = "获取项目成员列表", description = "获取项目的所有成员")
    @GetMapping
    public ApiResponse<List<ProjectMemberResponseDTO>> getProjectMembers(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("获取项目成员列表: projectId={}", projectId);
        
        List<ProjectMember> members = projectMemberService.getProjectMembers(projectId);
        List<ProjectMemberResponseDTO> memberDTOs = members.stream()
                .map(ProjectMemberResponseDTO::from)
                .collect(Collectors.toList());
        
        return ApiResponse.success(memberDTOs, "查询成功");
    }
    
    /**
     * 分页查询项目成员
     */
    @Operation(summary = "分页查询项目成员", description = "分页查询项目的成员列表")
    @GetMapping("/page")
    public ApiResponse<PageResponseDTO<ProjectMemberResponseDTO>> getProjectMembersPage(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        logger.info("分页查询项目成员: projectId={}, page={}, size={}", projectId, page, size);
        
        Page<ProjectMember> memberPage = projectMemberService.getProjectMembers(projectId, page, size);
        
        List<ProjectMemberResponseDTO> memberDTOs = memberPage.getContent().stream()
                .map(ProjectMemberResponseDTO::from)
                .collect(Collectors.toList());
        
        PageResponseDTO<ProjectMemberResponseDTO> pageResponse = PageResponseDTO.<ProjectMemberResponseDTO>builder()
                .content(memberDTOs)
                .page(memberPage.getNumber())
                .size(memberPage.getSize())
                .totalElements(memberPage.getTotalElements())
                .totalPages(memberPage.getTotalPages())
                .first(memberPage.isFirst())
                .last(memberPage.isLast())
                .build();
        
        return ApiResponse.success(pageResponse, "查询成功");
    }
    
    /**
     * 根据角色查询项目成员
     */
    @Operation(summary = "根据角色查询成员", description = "根据角色查询项目成员")
    @GetMapping("/role/{role}")
    public ApiResponse<List<ProjectMemberResponseDTO>> getProjectMembersByRole(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "角色") @PathVariable ProjectMemberRole role) {
        logger.info("根据角色查询项目成员: projectId={}, role={}", projectId, role);
        
        List<ProjectMember> members = projectMemberService.getProjectMembersByRole(projectId, role);
        List<ProjectMemberResponseDTO> memberDTOs = members.stream()
                .map(ProjectMemberResponseDTO::simple)
                .collect(Collectors.toList());
        
        return ApiResponse.success(memberDTOs, "查询成功");
    }
    
    /**
     * 获取项目管理员
     */
    @Operation(summary = "获取项目管理员", description = "获取项目的管理员列表")
    @GetMapping("/managers")
    public ApiResponse<List<ProjectMemberResponseDTO>> getProjectManagers(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("获取项目管理员: projectId={}", projectId);
        
        List<ProjectMember> managers = projectMemberService.getProjectManagers(projectId);
        List<ProjectMemberResponseDTO> managerDTOs = managers.stream()
                .map(ProjectMemberResponseDTO::simple)
                .collect(Collectors.toList());
        
        return ApiResponse.success(managerDTOs, "查询成功");
    }
    
    /**
     * 获取项目经理
     */
    @Operation(summary = "获取项目经理", description = "获取项目的项目经理列表")
    @GetMapping("/owners")
    public ApiResponse<List<ProjectMemberResponseDTO>> getProjectOwners(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("获取项目经理: projectId={}", projectId);
        
        List<ProjectMember> owners = projectMemberService.getProjectOwners(projectId);
        List<ProjectMemberResponseDTO> ownerDTOs = owners.stream()
                .map(ProjectMemberResponseDTO::simple)
                .collect(Collectors.toList());
        
        return ApiResponse.success(ownerDTOs, "查询成功");
    }
    
    /**
     * 获取用户在项目中的信息
     */
    @Operation(summary = "获取用户在项目中的信息", description = "获取指定用户在项目中的成员信息")
    @GetMapping("/user/{userId}")
    public ApiResponse<ProjectMemberResponseDTO> getUserProjectMember(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        logger.info("获取用户在项目中的信息: projectId={}, userId={}", projectId, userId);
        
        ProjectMember member = projectMemberService.getUserProjectMember(projectId, userId);
        if (member == null) {
            return ApiResponse.success(null, "用户不是项目成员");
        }
        
        ProjectMemberResponseDTO responseDTO = ProjectMemberResponseDTO.from(member);
        return ApiResponse.success(responseDTO, "查询成功");
    }
    
    // ========== 权限和统计 ==========
    
    /**
     * 获取用户在项目中的权限
     */
    @Operation(summary = "获取用户权限", description = "获取用户在项目中的权限信息")
    @GetMapping("/permissions/{userId}")
    public ApiResponse<Map<String, Boolean>> getUserPermissions(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        logger.info("获取用户权限: projectId={}, userId={}", projectId, userId);
        
        Map<String, Boolean> permissions = projectMemberService.getUserPermissionsInProject(projectId, userId);
        
        return ApiResponse.success(permissions, "查询成功");
    }
    
    /**
     * 获取我在项目中的权限
     */
    @Operation(summary = "获取我的权限", description = "获取当前用户在项目中的权限信息")
    @GetMapping("/my/permissions")
    public ApiResponse<Map<String, Boolean>> getMyPermissions(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("获取我的权限: projectId={}", projectId);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Map<String, Boolean> permissions = projectMemberService.getUserPermissionsInProject(projectId, currentUserId);
        
        return ApiResponse.success(permissions, "查询成功");
    }
    
    /**
     * 获取项目成员统计信息
     */
    @Operation(summary = "获取成员统计信息", description = "获取项目的成员统计信息")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getProjectMemberStatistics(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("获取项目成员统计信息: projectId={}", projectId);
        
        Map<String, Object> statistics = projectMemberService.getProjectMemberStatistics(projectId);
        
        return ApiResponse.success(statistics, "查询成功");
    }
    
    // ========== 批量操作 ==========
    
    /**
     * 批量添加项目成员
     */
    @Operation(summary = "批量添加成员", description = "批量向项目中添加成员")
    @PostMapping("/batch")
    public ApiResponse<List<ProjectMemberResponseDTO>> addProjectMembersBatch(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID列表") @RequestParam List<Long> userIds,
            @Parameter(description = "角色") @RequestParam ProjectMemberRole role) {
        logger.info("批量添加项目成员: projectId={}, userCount={}, role={}", projectId, userIds.size(), role);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        List<ProjectMember> members = projectMemberService.addProjectMembers(projectId, userIds, role, currentUserId);
        List<ProjectMemberResponseDTO> memberDTOs = members.stream()
                .map(ProjectMemberResponseDTO::simple)
                .collect(Collectors.toList());
        
        logger.info("批量添加项目成员成功: projectId={}, addedCount={}", projectId, members.size());
        return ApiResponse.success(memberDTOs, String.format("成功添加 %d 个成员", members.size()));
    }
    
    /**
     * 批量移除项目成员
     */
    @Operation(summary = "批量移除成员", description = "批量从项目中移除成员")
    @DeleteMapping("/batch")
    public ApiResponse<Void> removeProjectMembersBatch(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "用户ID列表") @RequestParam List<Long> userIds) {
        logger.info("批量移除项目成员: projectId={}, userCount={}", projectId, userIds.size());
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        projectMemberService.removeProjectMembers(projectId, userIds, currentUserId);
        
        logger.info("批量移除项目成员成功: projectId={}, userCount={}", projectId, userIds.size());
        return ApiResponse.success(null, "批量移除成员成功");
    }
}