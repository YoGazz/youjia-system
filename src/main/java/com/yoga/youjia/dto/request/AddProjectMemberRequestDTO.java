package com.yoga.youjia.dto.request;

import com.yoga.youjia.common.enums.ProjectMemberRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加项目成员请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProjectMemberRequestDTO {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 项目角色
     */
    @NotNull(message = "项目角色不能为空")
    private ProjectMemberRole role;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息不能超过500个字符")
    private String remarks;
}