package com.yoga.youjia.entity;

import com.yoga.youjia.common.enums.TestCasePriority;
import com.yoga.youjia.common.enums.TestCaseStatus;
import com.yoga.youjia.common.enums.TestCaseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试用例实体类
 * 
 * 测试用例是测试平台的核心实体，包含完整的测试信息
 */
@Entity
@Table(name = "test_cases", indexes = {
    @Index(name = "idx_test_case_project_id", columnList = "project_id"),
    @Index(name = "idx_test_case_module_id", columnList = "module_id"),
    @Index(name = "idx_test_case_case_id", columnList = "case_id", unique = true),
    @Index(name = "idx_test_case_priority", columnList = "priority"),
    @Index(name = "idx_test_case_status", columnList = "status"),
    @Index(name = "idx_test_case_type", columnList = "type"),
    @Index(name = "idx_test_case_created_by", columnList = "created_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TestCase {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用例编号（业务唯一标识）
     * 格式：TC_{项目编码}_{模块编码}_{序号}
     * 例如：TC_YJ_LOGIN_001
     */
    @Column(name = "case_id", nullable = false, unique = true, length = 100)
    private String caseId;
    
    /**
     * 用例标题
     */
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    /**
     * 用例描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 前置条件
     */
    @Column(name = "preconditions", columnDefinition = "TEXT")
    private String preconditions;
    
    /**
     * 测试数据
     */
    @Column(name = "test_data", columnDefinition = "TEXT")
    private String testData;
    
    /**
     * 预期结果
     */
    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;
    
    /**
     * 后置条件（清理步骤）
     */
    @Column(name = "postconditions", columnDefinition = "TEXT")
    private String postconditions;
    
    /**
     * 测试用例类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    @Builder.Default
    private TestCaseType type = TestCaseType.FUNCTIONAL;
    
    /**
     * 优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    @Builder.Default
    private TestCasePriority priority = TestCasePriority.MEDIUM;
    
    /**
     * 用例状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private TestCaseStatus status = TestCaseStatus.DRAFT;
    
    /**
     * 自动化标识
     */
    @Column(name = "automated", nullable = false)
    @Builder.Default
    private Boolean automated = false;
    
    /**
     * 自动化脚本路径
     */
    @Column(name = "automation_script", length = 500)
    private String automationScript;
    
    /**
     * 标签（以逗号分隔）
     */
    @Column(name = "tags", length = 1000)
    private String tags;
    
    /**
     * 所属项目ID
     */
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    /**
     * 所属模块ID
     */
    @Column(name = "module_id", nullable = false)
    private Long moduleId;
    
    /**
     * 关联需求ID
     */
    @Column(name = "requirement_id")
    private Long requirementId;
    
    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    /**
     * 预估执行时间（分钟）
     */
    @Column(name = "estimated_time")
    @Builder.Default
    private Integer estimatedTime = 5;
    
    /**
     * 用例版本号
     */
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;
    
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
     * 审核人ID
     */
    @Column(name = "reviewed_by")
    private Long reviewedBy;
    
    /**
     * 审核时间
     */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    /**
     * 审核意见
     */
    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;
    
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
     * 所属模块（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    private TestModule testModule;
    
    /**
     * 创建人（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User creator;
    
    /**
     * 更新人（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", insertable = false, updatable = false)
    private User updater;
    
    /**
     * 审核人（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", insertable = false, updatable = false)
    private User reviewer;
    
    /**
     * 测试步骤列表（一对多）
     */
    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<TestStep> testSteps = new ArrayList<>();
    
    // ========== 业务方法 ==========
    
    /**
     * 添加测试步骤
     */
    public void addTestStep(TestStep testStep) {
        if (testSteps == null) {
            testSteps = new ArrayList<>();
        }
        testSteps.add(testStep);
        testStep.setTestCase(this);
        testStep.setTestCaseId(this.id);
        
        // 自动设置步骤序号
        if (testStep.getStepOrder() == null) {
            testStep.setStepOrder(testSteps.size());
        }
    }
    
    /**
     * 移除测试步骤
     */
    public void removeTestStep(TestStep testStep) {
        if (testSteps != null) {
            testSteps.remove(testStep);
            testStep.setTestCase(null);
            testStep.setTestCaseId(null);
        }
    }
    
    /**
     * 获取标签列表
     */
    public List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> tagList = new ArrayList<>();
        String[] tagArray = tags.split(",");
        for (String tag : tagArray) {
            String trimmedTag = tag.trim();
            if (!trimmedTag.isEmpty()) {
                tagList.add(trimmedTag);
            }
        }
        return tagList;
    }
    
    /**
     * 设置标签列表
     */
    public void setTagList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = String.join(",", tagList);
        }
    }
    
    /**
     * 检查是否包含指定标签
     */
    public boolean hasTag(String tag) {
        return getTagList().contains(tag);
    }
    
    /**
     * 获取用例步骤总数
     */
    public int getStepCount() {
        return testSteps != null ? testSteps.size() : 0;
    }
    
    /**
     * 检查是否可以执行
     */
    public boolean canExecute() {
        return enabled && status.canExecute();
    }
    
    /**
     * 检查是否可以编辑
     */
    public boolean canEdit() {
        return enabled && status.canEdit();
    }
    
    /**
     * 检查是否可以审核
     */
    public boolean canReview() {
        return enabled && status.canReview();
    }
    
    /**
     * 提交审核
     */
    public void submitForReview() {
        if (status == TestCaseStatus.DRAFT || status == TestCaseStatus.REJECTED) {
            this.status = TestCaseStatus.PENDING_REVIEW;
        } else {
            throw new IllegalStateException("当前状态不允许提交审核: " + status);
        }
    }
    
    /**
     * 开始审核
     */
    public void startReview(Long reviewerId) {
        if (status == TestCaseStatus.PENDING_REVIEW) {
            this.status = TestCaseStatus.UNDER_REVIEW;
            this.reviewedBy = reviewerId;
            this.reviewedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("当前状态不允许开始审核: " + status);
        }
    }
    
    /**
     * 审核通过
     */
    public void approveReview(Long reviewerId, String comment) {
        if (status == TestCaseStatus.UNDER_REVIEW || status == TestCaseStatus.PENDING_REVIEW) {
            this.status = TestCaseStatus.APPROVED;
            this.reviewedBy = reviewerId;
            this.reviewedAt = LocalDateTime.now();
            this.reviewComment = comment;
        } else {
            throw new IllegalStateException("当前状态不允许审核通过: " + status);
        }
    }
    
    /**
     * 审核拒绝
     */
    public void rejectReview(Long reviewerId, String comment) {
        if (status == TestCaseStatus.UNDER_REVIEW || status == TestCaseStatus.PENDING_REVIEW) {
            this.status = TestCaseStatus.REJECTED;
            this.reviewedBy = reviewerId;
            this.reviewedAt = LocalDateTime.now();
            this.reviewComment = comment;
        } else {
            throw new IllegalStateException("当前状态不允许审核拒绝: " + status);
        }
    }
    
    /**
     * 激活用例
     */
    public void activate() {
        if (status == TestCaseStatus.APPROVED) {
            this.status = TestCaseStatus.ACTIVE;
        } else {
            throw new IllegalStateException("只有已通过审核的用例才能激活: " + status);
        }
    }
    
    /**
     * 废弃用例
     */
    public void deprecate() {
        this.status = TestCaseStatus.DEPRECATED;
        this.enabled = false;
    }
    
    /**
     * 归档用例
     */
    public void archive() {
        this.status = TestCaseStatus.ARCHIVED;
        this.enabled = false;
    }
    
    /**
     * 复制用例（创建副本）
     */
    public TestCase copy(String newTitle, String newCaseId) {
        TestCase copy = TestCase.builder()
                .caseId(newCaseId)
                .title(newTitle)
                .description(this.description)
                .preconditions(this.preconditions)
                .testData(this.testData)
                .expectedResult(this.expectedResult)
                .postconditions(this.postconditions)
                .type(this.type)
                .priority(this.priority)
                .status(TestCaseStatus.DRAFT)
                .automated(this.automated)
                .automationScript(this.automationScript)
                .tags(this.tags)
                .projectId(this.projectId)
                .moduleId(this.moduleId)
                .requirementId(this.requirementId)
                .estimatedTime(this.estimatedTime)
                .version(1)
                .enabled(true)
                .build();
        
        // 复制测试步骤
        if (this.testSteps != null) {
            for (TestStep step : this.testSteps) {
                TestStep copyStep = step.copy();
                copy.addTestStep(copyStep);
            }
        }
        
        return copy;
    }
    
    @Override
    public String toString() {
        return "TestCase{" +
                "id=" + id +
                ", caseId='" + caseId + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                ", status=" + status +
                ", projectId=" + projectId +
                ", moduleId=" + moduleId +
                ", enabled=" + enabled +
                '}';
    }
}