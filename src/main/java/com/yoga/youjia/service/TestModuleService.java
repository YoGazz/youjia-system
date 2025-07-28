package com.yoga.youjia.service;

import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import com.yoga.youjia.dto.request.CreateTestModuleRequestDTO;
import com.yoga.youjia.entity.TestModule;
import com.yoga.youjia.repository.TestCaseRepository;
import com.yoga.youjia.repository.TestModuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 测试模块服务类
 * 
 * 提供测试模块的层级管理功能
 */
@Slf4j
@Service
@Transactional
public class TestModuleService {
    
    @Autowired
    private TestModuleRepository testModuleRepository;
    
    @Autowired
    private TestCaseRepository testCaseRepository;
    
    /**
     * 创建测试模块
     */
    public TestModule createTestModule(CreateTestModuleRequestDTO requestDTO, Long projectId, Long createdBy) {
        log.info("创建测试模块: projectId={}, name={}, parentId={}", 
                projectId, requestDTO.getName(), requestDTO.getParentId());
        
        // 验证父模块
        TestModule parentModule = null;
        if (requestDTO.getParentId() != null) {
            parentModule = testModuleRepository.findById(requestDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, "父模块不存在"));
            
            if (!parentModule.getProjectId().equals(projectId)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "父模块不属于指定项目");
            }
        }
        
        // 检查同级模块名称是否重复
        if (testModuleRepository.existsByProjectIdAndParentIdAndNameAndEnabledTrue(
                projectId, requestDTO.getParentId(), requestDTO.getName())) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "同级模块中已存在相同名称的模块");
        }
        
        // 获取下一个排序序号
        Integer sortOrder = requestDTO.getSortOrder();
        if (sortOrder == null) {
            sortOrder = testModuleRepository.findMaxSortOrderByParent(projectId, requestDTO.getParentId()) + 1;
        }
        
        // 构建测试模块
        TestModule testModule = TestModule.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .projectId(projectId)
                .parentId(requestDTO.getParentId())
                .sortOrder(sortOrder)
                .enabled(true)
                .createdBy(createdBy)
                .build();
        
        // 设置层级信息
        if (parentModule != null) {
            testModule.setDepth(parentModule.getDepth() + 1);
            testModule.setModulePath(
                    (parentModule.getModulePath() != null ? parentModule.getModulePath() : "") + "/" + parentModule.getName()
            );
        } else {
            testModule.setDepth(1);
            testModule.setModulePath("");
        }
        
        testModule = testModuleRepository.save(testModule);
        log.info("测试模块创建成功: id={}, name={}", testModule.getId(), testModule.getName());
        return testModule;
    }
    
    /**
     * 获取项目模块树
     */
    @Transactional(readOnly = true)
    public List<TestModule> getProjectModuleTree(Long projectId) {
        log.debug("获取项目模块树: projectId={}", projectId);
        return testModuleRepository.findModuleTreeByProjectId(projectId);
    }
    
    /**
     * 获取根模块列表
     */
    @Transactional(readOnly = true)
    public List<TestModule> getRootModules(Long projectId) {
        log.debug("获取根模块列表: projectId={}", projectId);
        return testModuleRepository.findByProjectIdAndParentIdIsNullAndEnabledTrueOrderBySortOrderAscIdAsc(projectId);
    }
    
    /**
     * 获取子模块列表
     */
    @Transactional(readOnly = true)
    public List<TestModule> getChildModules(Long projectId, Long parentId) {
        log.debug("获取子模块列表: projectId={}, parentId={}", projectId, parentId);
        return testModuleRepository.findByProjectIdAndParentIdAndEnabledTrueOrderBySortOrderAscIdAsc(projectId, parentId);
    }
    
    /**
     * 根据ID获取测试模块
     */
    @Transactional(readOnly = true)
    public TestModule getTestModuleById(Long id) {
        return testModuleRepository.findById(id)
                .filter(TestModule::getEnabled)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, "测试模块不存在"));
    }
    
    /**
     * 更新测试模块
     */
    public TestModule updateTestModule(Long id, CreateTestModuleRequestDTO requestDTO, Long updatedBy) {
        log.info("更新测试模块: id={}, name={}", id, requestDTO.getName());
        
        TestModule testModule = getTestModuleById(id);
        
        // 检查同级模块名称是否重复（排除自己）
        if (testModuleRepository.existsByProjectIdAndParentIdAndNameAndEnabledTrueAndIdNot(
                testModule.getProjectId(), testModule.getParentId(), requestDTO.getName(), id)) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "同级模块中已存在相同名称的模块");
        }
        
        // 更新基本信息
        testModule.setName(requestDTO.getName());
        testModule.setDescription(requestDTO.getDescription());
        testModule.setUpdatedBy(updatedBy);
        
        // 如果排序序号发生变化
        if (requestDTO.getSortOrder() != null && !requestDTO.getSortOrder().equals(testModule.getSortOrder())) {
            testModule.setSortOrder(requestDTO.getSortOrder());
        }
        
        testModule = testModuleRepository.save(testModule);
        
        // 更新子模块的路径（如果名称发生变化）
        updateChildrenPath(testModule);
        
        log.info("测试模块更新成功: id={}", testModule.getId());
        return testModule;
    }
    
    /**
     * 删除测试模块
     */
    public void deleteTestModule(Long id, Long deletedBy) {
        log.info("删除测试模块: id={}", id);
        
        TestModule testModule = getTestModuleById(id);
        
        // 检查是否有子模块
        if (testModuleRepository.existsByParentIdAndEnabledTrue(id)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "存在子模块，无法删除");
        }
        
        // 检查是否有测试用例
        long testCaseCount = testCaseRepository.countByModuleIdAndEnabledTrue(id);
        if (testCaseCount > 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模块下存在测试用例，无法删除");
        }
        
        // 软删除
        testModule.setEnabled(false);
        testModule.setUpdatedBy(deletedBy);
        testModuleRepository.save(testModule);
        
        log.info("测试模块删除成功: id={}", id);
    }
    
    /**
     * 移动模块
     */
    public TestModule moveModule(Long moduleId, Long newParentId, Long updatedBy) {
        log.info("移动测试模块: moduleId={}, newParentId={}", moduleId, newParentId);
        
        TestModule testModule = getTestModuleById(moduleId);
        
        // 验证新父模块
        TestModule newParentModule = null;
        if (newParentId != null) {
            newParentModule = getTestModuleById(newParentId);
            
            if (!newParentModule.getProjectId().equals(testModule.getProjectId())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "新父模块不属于同一项目");
            }
            
            // 检查是否会形成循环引用
            if (isAncestor(testModule, newParentModule)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "不能移动到自己的子模块下");
            }
        }
        
        // 检查新位置是否有重名模块
        if (testModuleRepository.existsByProjectIdAndParentIdAndNameAndEnabledTrueAndIdNot(
                testModule.getProjectId(), newParentId, testModule.getName(), moduleId)) {
            throw new BusinessException(ErrorCode.DATA_EXISTS, "目标位置已存在相同名称的模块");
        }
        
        // 更新模块信息
        testModule.setParentId(newParentId);
        testModule.setUpdatedBy(updatedBy);
        
        // 重新计算层级和路径
        if (newParentModule != null) {
            testModule.setDepth(newParentModule.getDepth() + 1);
            testModule.setModulePath(
                    (newParentModule.getModulePath() != null ? newParentModule.getModulePath() : "") + "/" + newParentModule.getName()
            );
        } else {
            testModule.setDepth(1);
            testModule.setModulePath("");
        }
        
        testModule = testModuleRepository.save(testModule);
        
        // 更新所有子模块的路径
        updateChildrenPath(testModule);
        
        log.info("测试模块移动成功: id={}", testModule.getId());
        return testModule;
    }
    
    /**
     * 调整模块排序
     */
    public void reorderModule(Long moduleId, Integer newOrder, Long updatedBy) {
        log.info("调整模块排序: moduleId={}, newOrder={}", moduleId, newOrder);
        
        TestModule testModule = getTestModuleById(moduleId);
        testModule.setSortOrder(newOrder);
        testModule.setUpdatedBy(updatedBy);
        
        testModuleRepository.save(testModule);
        log.info("模块排序调整成功: id={}", testModule.getId());
    }
    
    /**
     * 获取模块统计信息
     */
    @Transactional(readOnly = true)
    public ModuleStatistics getModuleStatistics(Long moduleId) {
        log.debug("获取模块统计信息: moduleId={}", moduleId);
        
        TestModule testModule = getTestModuleById(moduleId);
        ModuleStatistics statistics = new ModuleStatistics();
        
        // 直接测试用例数
        statistics.setDirectTestCaseCount(testCaseRepository.countByModuleIdAndEnabledTrue(moduleId));
        
        // 递归测试用例数（包含子模块）
        String modulePath = (testModule.getModulePath() != null ? testModule.getModulePath() : "") + "/" + testModule.getName();
        statistics.setTotalTestCaseCount(testModuleRepository.countTestCasesByModuleRecursive(
                testModule.getProjectId(), moduleId, modulePath));
        
        // 子模块数
        statistics.setChildModuleCount((long) testModule.getChildren().size());
        
        return statistics;
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 检查是否为祖先节点（防止循环引用）
     */
    private boolean isAncestor(TestModule ancestor, TestModule descendant) {
        TestModule current = descendant;
        while (current.getParentId() != null) {
            if (current.getParentId().equals(ancestor.getId())) {
                return true;
            }
            current = testModuleRepository.findById(current.getParentId()).orElse(null);
            if (current == null) {
                break;
            }
        }
        return false;
    }
    
    /**
     * 更新子模块路径
     */
    private void updateChildrenPath(TestModule parentModule) {
        List<TestModule> children = testModuleRepository.findByProjectIdAndParentIdAndEnabledTrueOrderBySortOrderAscIdAsc(
                parentModule.getProjectId(), parentModule.getId());
        
        for (TestModule child : children) {
            child.buildModulePath();
            testModuleRepository.save(child);
            updateChildrenPath(child); // 递归更新
        }
    }
    
    /**
     * 模块统计信息类
     */
    public static class ModuleStatistics {
        private Long directTestCaseCount = 0L;
        private Long totalTestCaseCount = 0L;
        private Long childModuleCount = 0L;
        
        // Getters and Setters
        public Long getDirectTestCaseCount() { return directTestCaseCount; }
        public void setDirectTestCaseCount(Long directTestCaseCount) { this.directTestCaseCount = directTestCaseCount; }
        
        public Long getTotalTestCaseCount() { return totalTestCaseCount; }
        public void setTotalTestCaseCount(Long totalTestCaseCount) { this.totalTestCaseCount = totalTestCaseCount; }
        
        public Long getChildModuleCount() { return childModuleCount; }
        public void setChildModuleCount(Long childModuleCount) { this.childModuleCount = childModuleCount; }
    }
}