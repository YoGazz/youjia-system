package com.yoga.youjia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试模块实体类
 * 
 * 用于组织和管理测试用例的模块结构，支持树形层级结构
 */
@Entity
@Table(name = "test_modules", indexes = {
    @Index(name = "idx_test_module_project_id", columnList = "project_id"),
    @Index(name = "idx_test_module_parent_id", columnList = "parent_id"),
    @Index(name = "idx_test_module_path", columnList = "module_path")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestModule {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 模块名称
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    /**
     * 模块描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 所属项目ID
     */
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    /**
     * 父模块ID（为null表示根模块）
     */
    @Column(name = "parent_id")
    private Long parentId;
    
    /**
     * 模块路径（用于快速查询和显示层级关系）
     * 格式：/根模块/子模块/孙模块
     */
    @Column(name = "module_path", length = 1000)
    private String modulePath;
    
    /**
     * 模块层级深度（根模块为1）
     */
    @Column(name = "depth", nullable = false)
    @Builder.Default
    private Integer depth = 1;
    
    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    /**
     * 创建人ID
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ========== 关联关系 ==========
    
    /**
     * 所属项目（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;
    
    /**
     * 父模块（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private TestModule parent;
    
    /**
     * 子模块列表（一对多）
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, id ASC")
    @Builder.Default
    private List<TestModule> children = new ArrayList<>();
    
    /**
     * 模块下的测试用例（一对多）
     */
    @OneToMany(mappedBy = "testModule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, id ASC")
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();
    
    // ========== 业务方法 ==========
    
    /**
     * 是否为根模块
     */
    public boolean isRoot() {
        return parentId == null;
    }
    
    /**
     * 是否为叶子模块（没有子模块）
     */
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
    
    /**
     * 获取完整的模块名称（包含父级路径）
     */
    public String getFullName() {
        if (modulePath == null) {
            return name;
        }
        return modulePath.replace("/", " > ") + " > " + name;
    }
    
    /**
     * 添加子模块
     */
    public void addChild(TestModule child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.setParent(this);
        child.setParentId(this.id);
        child.setProjectId(this.projectId);
        child.setDepth(this.depth + 1);
        
        // 更新模块路径
        String parentPath = this.modulePath != null ? this.modulePath : "";
        child.setModulePath(parentPath + "/" + this.name);
    }
    
    /**
     * 移除子模块
     */
    public void removeChild(TestModule child) {
        if (children != null) {
            children.remove(child);
            child.setParent(null);
            child.setParentId(null);
        }
    }
    
    /**
     * 添加测试用例
     */
    public void addTestCase(TestCase testCase) {
        if (testCases == null) {
            testCases = new ArrayList<>();
        }
        testCases.add(testCase);
        testCase.setTestModule(this);
        testCase.setModuleId(this.id);
        testCase.setProjectId(this.projectId);
    }
    
    /**
     * 移除测试用例
     */
    public void removeTestCase(TestCase testCase) {
        if (testCases != null) {
            testCases.remove(testCase);
            testCase.setTestModule(null);
            testCase.setModuleId(null);
        }
    }
    
    /**
     * 获取模块下测试用例总数（包含子模块）
     */
    public int getTotalTestCaseCount() {
        int count = testCases != null ? testCases.size() : 0;
        if (children != null) {
            for (TestModule child : children) {
                count += child.getTotalTestCaseCount();
            }
        }
        return count;
    }
    
    /**
     * 构建模块路径
     */
    public void buildModulePath() {
        if (parent == null) {
            this.modulePath = "";
            this.depth = 1;
        } else {
            this.modulePath = (parent.getModulePath() != null ? parent.getModulePath() : "") + "/" + parent.getName();
            this.depth = parent.getDepth() + 1;
        }
    }
    
    @Override
    public String toString() {
        return "TestModule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projectId=" + projectId +
                ", parentId=" + parentId +
                ", modulePath='" + modulePath + '\'' +
                ", depth=" + depth +
                ", enabled=" + enabled +
                '}';
    }
}