package com.yoga.youjia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 测试步骤实体类
 * 
 * 测试步骤是测试用例的详细执行步骤
 */
@Entity
@Table(name = "test_steps", indexes = {
    @Index(name = "idx_test_step_case_id", columnList = "test_case_id"),
    @Index(name = "idx_test_step_order", columnList = "step_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TestStep {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 所属测试用例ID
     */
    @Column(name = "test_case_id", nullable = false)
    private Long testCaseId;
    
    /**
     * 步骤序号
     */
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;
    
    /**
     * 步骤描述（操作内容）
     */
    @Column(name = "step_description", nullable = false, columnDefinition = "TEXT")
    private String stepDescription;
    
    /**
     * 测试数据
     */
    @Column(name = "test_data", columnDefinition = "TEXT")
    private String testData;
    
    /**
     * 预期结果
     */
    @Column(name = "expected_result", nullable = false, columnDefinition = "TEXT")
    private String expectedResult;
    
    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    /**
     * 是否为关键步骤
     */
    @Column(name = "is_key_step", nullable = false)
    @Builder.Default
    private Boolean isKeyStep = false;
    
    /**
     * 自动化标识
     */
    @Column(name = "automated", nullable = false)
    @Builder.Default
    private Boolean automated = false;
    
    /**
     * 自动化脚本代码
     */
    @Column(name = "automation_code", columnDefinition = "TEXT")
    private String automationCode;
    
    /**
     * 预估执行时间（秒）
     */
    @Column(name = "estimated_time")
    @Builder.Default
    private Integer estimatedTime = 30;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
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
     * 所属测试用例（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", insertable = false, updatable = false)
    private TestCase testCase;
    
    // ========== 业务方法 ==========
    
    /**
     * 检查步骤是否完整
     */
    public boolean isComplete() {
        return stepDescription != null && !stepDescription.trim().isEmpty() &&
               expectedResult != null && !expectedResult.trim().isEmpty();
    }
    
    /**
     * 检查是否可以自动化
     */
    public boolean canAutomate() {
        return automationCode != null && !automationCode.trim().isEmpty();
    }
    
    /**
     * 获取步骤的简短描述（用于列表显示）
     */
    public String getShortDescription() {
        if (stepDescription == null) {
            return "";
        }
        if (stepDescription.length() <= 50) {
            return stepDescription;
        }
        return stepDescription.substring(0, 47) + "...";
    }
    
    /**
     * 获取预期结果的简短描述
     */
    public String getShortExpectedResult() {
        if (expectedResult == null) {
            return "";
        }
        if (expectedResult.length() <= 50) {
            return expectedResult;
        }
        return expectedResult.substring(0, 47) + "...";
    }
    
    /**
     * 复制步骤（创建副本）
     */
    public TestStep copy() {
        return TestStep.builder()
                .stepOrder(this.stepOrder)
                .stepDescription(this.stepDescription)
                .testData(this.testData)
                .expectedResult(this.expectedResult)
                .remark(this.remark)
                .isKeyStep(this.isKeyStep)
                .automated(this.automated)
                .automationCode(this.automationCode)
                .estimatedTime(this.estimatedTime)
                .enabled(this.enabled)
                .build();
    }
    
    /**
     * 移动到指定位置
     */
    public void moveTo(int newOrder) {
        this.stepOrder = newOrder;
    }
    
    /**
     * 标记为关键步骤
     */
    public void markAsKeyStep() {
        this.isKeyStep = true;
    }
    
    /**
     * 取消关键步骤标记
     */
    public void unmarkAsKeyStep() {
        this.isKeyStep = false;
    }
    
    /**
     * 启用自动化
     */
    public void enableAutomation(String automationCode) {
        this.automated = true;
        this.automationCode = automationCode;
    }
    
    /**
     * 禁用自动化
     */
    public void disableAutomation() {
        this.automated = false;
        this.automationCode = null;
    }
    
    @Override
    public String toString() {
        return "TestStep{" +
                "id=" + id +
                ", testCaseId=" + testCaseId +
                ", stepOrder=" + stepOrder +
                ", stepDescription='" + getShortDescription() + '\'' +
                ", isKeyStep=" + isKeyStep +
                ", automated=" + automated +
                ", enabled=" + enabled +
                '}';
    }
}