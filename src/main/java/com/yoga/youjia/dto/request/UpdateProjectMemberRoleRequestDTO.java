package com.yoga.youjia.dto.request;

import com.yoga.youjia.common.enums.ProjectMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新项目成员角色请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectMemberRoleRequestDTO {
    
    /**
     * 新角色
     */
    @NotNull(message = "新角色不能为空")
    private ProjectMemberRole role;
}