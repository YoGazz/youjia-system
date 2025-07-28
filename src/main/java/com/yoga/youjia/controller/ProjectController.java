package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import com.yoga.youjia.common.enums.ProjectStatus;
import com.yoga.youjia.dto.request.CreateProjectRequestDTO;
import com.yoga.youjia.dto.request.ProjectQueryRequestDTO;
import com.yoga.youjia.dto.request.UpdateProjectRequestDTO;
import com.yoga.youjia.dto.response.PageResponseDTO;
import com.yoga.youjia.dto.response.ProjectResponseDTO;
import com.yoga.youjia.entity.Project;
import com.yoga.youjia.service.ProjectService;
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
 * 项目管理控制器
 * 
 * 提供项目的增删改查、状态管理等RESTful API
 */
@Tag(name = "项目管理", description = "项目的创建、查询、更新、删除等管理功能")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    
    @Autowired
    private ProjectService projectService;
    
    // ========== 项目基本操作 ==========
    
    /**
     * 创建项目
     */
    @Operation(summary = "创建项目", description = "创建新的测试项目")
    @PostMapping
    public ApiResponse<ProjectResponseDTO> createProject(
            @Valid @RequestBody CreateProjectRequestDTO requestDTO) {
        logger.info("创建项目请求: code={}, name={}", requestDTO.getCode(), requestDTO.getName());
        
        // 构建项目实体
        Project project = Project.builder()
                .code(requestDTO.getCode())
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .priority(requestDTO.getPriority())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .version(requestDTO.getVersion())
                .settings(requestDTO.getSettings())
                .build();
        
        // 设置标签
        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            project.setTagList(requestDTO.getTags());
        }
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Project createdProject = projectService.createProject(project, currentUserId);
        ProjectResponseDTO responseDTO = ProjectResponseDTO.from(createdProject);
        
        logger.info("项目创建成功: id={}, code={}", createdProject.getId(), createdProject.getCode());
        return ApiResponse.success(responseDTO, "项目创建成功");
    }
    
    /**
     * 根据ID查询项目详情
     */
    @Operation(summary = "查询项目详情", description = "根据项目ID查询项目详细信息")
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponseDTO> getProject(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("查询项目详情: projectId={}", projectId);
        
        Project project = projectService.getProjectById(projectId);
        ProjectResponseDTO responseDTO = ProjectResponseDTO.from(project);
        
        return ApiResponse.success(responseDTO, "查询成功");
    }
    
    /**
     * 根据编码查询项目详情
     */
    @Operation(summary = "根据编码查询项目", description = "根据项目编码查询项目详细信息")
    @GetMapping("/code/{code}")
    public ApiResponse<ProjectResponseDTO> getProjectByCode(
            @Parameter(description = "项目编码") @PathVariable String code) {
        logger.info("根据编码查询项目: code={}", code);
        
        Project project = projectService.getProjectByCode(code);
        ProjectResponseDTO responseDTO = ProjectResponseDTO.from(project);
        
        return ApiResponse.success(responseDTO, "查询成功");
    }
    
    /**
     * 更新项目信息
     */
    @Operation(summary = "更新项目信息", description = "更新项目的基本信息")
    @PutMapping("/{projectId}")
    public ApiResponse<ProjectResponseDTO> updateProject(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequestDTO requestDTO) {
        logger.info("更新项目信息: projectId={}", projectId);
        
        // 构建更新数据
        Project updateData = Project.builder()
                .code(requestDTO.getCode())
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .priority(requestDTO.getPriority())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .version(requestDTO.getVersion())
                .settings(requestDTO.getSettings())
                .build();
        
        // 设置标签
        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            updateData.setTagList(requestDTO.getTags());
        }
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Project updatedProject = projectService.updateProject(projectId, updateData, currentUserId);
        ProjectResponseDTO responseDTO = ProjectResponseDTO.from(updatedProject);
        
        logger.info("项目信息更新成功: projectId={}", projectId);
        return ApiResponse.success(responseDTO, "项目信息更新成功");
    }
    
    /**
     * 更新项目状态
     */
    @Operation(summary = "更新项目状态", description = "更新项目的状态")
    @PatchMapping("/{projectId}/status")
    public ApiResponse<ProjectResponseDTO> updateProjectStatus(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "新状态") @RequestParam ProjectStatus status) {
        logger.info("更新项目状态: projectId={}, status={}", projectId, status);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Project updatedProject = projectService.updateProjectStatus(projectId, status, currentUserId);
        ProjectResponseDTO responseDTO = ProjectResponseDTO.from(updatedProject);
        
        logger.info("项目状态更新成功: projectId={}, status={}", projectId, status);
        return ApiResponse.success(responseDTO, "项目状态更新成功");
    }
    
    /**
     * 启用/禁用项目
     */
    @Operation(summary = "启用或禁用项目", description = "启用或禁用指定项目")
    @PatchMapping("/{projectId}/enabled")
    public ApiResponse<ProjectResponseDTO> toggleProjectEnabled(
            @Parameter(description = "项目ID") @PathVariable Long projectId,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        logger.info("{}项目: projectId={}", enabled ? "启用" : "禁用", projectId);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Project updatedProject = projectService.toggleProjectEnabled(projectId, enabled, currentUserId);
        ProjectResponseDTO responseDTO = ProjectResponseDTO.from(updatedProject);
        
        logger.info("项目{}成功: projectId={}", enabled ? "启用" : "禁用", projectId);
        return ApiResponse.success(responseDTO, String.format("项目%s成功", enabled ? "启用" : "禁用"));
    }
    
    /**
     * 删除项目
     */
    @Operation(summary = "删除项目", description = "删除指定项目（逻辑删除）")
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> deleteProject(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        logger.info("删除项目: projectId={}", projectId);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        projectService.deleteProject(projectId, currentUserId);
        
        logger.info("项目删除成功: projectId={}", projectId);
        return ApiResponse.success(null, "项目删除成功");
    }
    
    // ========== 项目查询操作 ==========
    
    /**
     * 分页查询项目列表
     */
    @Operation(summary = "分页查询项目", description = "根据条件分页查询项目列表")
    @GetMapping
    public ApiResponse<PageResponseDTO<ProjectResponseDTO>> getProjects(
            @ModelAttribute @Valid ProjectQueryRequestDTO queryDTO) {
        logger.info("分页查询项目: page={}, size={}, keyword={}", 
                   queryDTO.getPage(), queryDTO.getSize(), queryDTO.getKeyword());
        
        Page<Project> projectPage = projectService.searchProjects(
            queryDTO.getStatus(), queryDTO.getCreatedBy(), queryDTO.getKeyword(),
            queryDTO.getStartDateFrom(), queryDTO.getEndDateFrom(),
            queryDTO.getPage(), queryDTO.getSize(), queryDTO.getSortBy(), queryDTO.getSortDir());
        
        List<ProjectResponseDTO> projectDTOs = projectPage.getContent().stream()
                .map(ProjectResponseDTO::from)
                .collect(Collectors.toList());
        
        PageResponseDTO<ProjectResponseDTO> pageResponse = PageResponseDTO.<ProjectResponseDTO>builder()
                .content(projectDTOs)
                .page(projectPage.getNumber())
                .size(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .first(projectPage.isFirst())
                .last(projectPage.isLast())
                .build();
        
        return ApiResponse.success(pageResponse, "查询成功");
    }
    
    /**
     * 查询我参与的项目
     */
    @Operation(summary = "查询我参与的项目", description = "查询当前用户参与的项目列表")
    @GetMapping("/my")
    public ApiResponse<PageResponseDTO<ProjectResponseDTO>> getMyProjects(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        logger.info("查询我参与的项目: page={}, size={}", page, size);
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Page<Project> projectPage = projectService.getProjectsByUser(currentUserId, page, size);
        
        List<ProjectResponseDTO> projectDTOs = projectPage.getContent().stream()
                .map(ProjectResponseDTO::simple)
                .collect(Collectors.toList());
        
        PageResponseDTO<ProjectResponseDTO> pageResponse = PageResponseDTO.<ProjectResponseDTO>builder()
                .content(projectDTOs)
                .page(projectPage.getNumber())
                .size(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .first(projectPage.isFirst())
                .last(projectPage.isLast())
                .build();
        
        return ApiResponse.success(pageResponse, "查询成功");
    }
    
    /**
     * 搜索项目
     */
    @Operation(summary = "搜索项目", description = "根据关键词搜索项目")
    @GetMapping("/search")
    public ApiResponse<List<ProjectResponseDTO>> searchProjects(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        logger.info("搜索项目: keyword={}", keyword);
        
        List<Project> projects = projectService.searchProjectsByKeyword(keyword);
        List<ProjectResponseDTO> projectDTOs = projects.stream()
                .map(ProjectResponseDTO::simple)
                .collect(Collectors.toList());
        
        return ApiResponse.success(projectDTOs, "搜索成功");
    }
    
    /**
     * 查询即将到期的项目
     */
    @Operation(summary = "查询即将到期的项目", description = "查询指定天数内即将到期的项目")
    @GetMapping("/near-deadline")
    public ApiResponse<List<ProjectResponseDTO>> getProjectsNearDeadline(
            @Parameter(description = "天数") @RequestParam(defaultValue = "7") int days) {
        logger.info("查询即将到期的项目: days={}", days);
        
        List<Project> projects = projectService.getProjectsNearDeadline(days);
        List<ProjectResponseDTO> projectDTOs = projects.stream()
                .map(ProjectResponseDTO::simple)
                .collect(Collectors.toList());
        
        return ApiResponse.success(projectDTOs, "查询成功");
    }
    
    /**
     * 查询逾期的项目
     */
    @Operation(summary = "查询逾期的项目", description = "查询已经逾期的项目")
    @GetMapping("/overdue")
    public ApiResponse<List<ProjectResponseDTO>> getOverdueProjects() {
        logger.info("查询逾期的项目");
        
        List<Project> projects = projectService.getOverdueProjects();
        List<ProjectResponseDTO> projectDTOs = projects.stream()
                .map(ProjectResponseDTO::simple)
                .collect(Collectors.toList());
        
        return ApiResponse.success(projectDTOs, "查询成功");
    }
    
    // ========== 统计信息 ==========
    
    /**
     * 获取项目统计信息
     */
    @Operation(summary = "获取项目统计信息", description = "获取项目的统计信息")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getProjectStatistics() {
        logger.info("获取项目统计信息");
        
        Map<String, Object> statistics = projectService.getProjectStatistics();
        
        return ApiResponse.success(statistics, "查询成功");
    }
    
    /**
     * 获取我的项目统计信息
     */
    @Operation(summary = "获取我的项目统计信息", description = "获取当前用户的项目统计信息")
    @GetMapping("/my/statistics")
    public ApiResponse<Map<String, Object>> getMyProjectStatistics() {
        logger.info("获取我的项目统计信息");
        
        // TODO: 从安全上下文获取当前用户ID
        Long currentUserId = 1L; // 临时硬编码，实际应从JWT中获取
        
        Map<String, Object> statistics = projectService.getUserProjectStatistics(currentUserId);
        
        return ApiResponse.success(statistics, "查询成功");
    }
}