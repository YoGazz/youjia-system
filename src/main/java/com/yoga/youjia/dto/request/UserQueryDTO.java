package com.yoga.youjia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询条件DTO
 *
 * 用于接收用户列表查询的过滤条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户查询条件")
public class UserQueryDTO {
    @Schema(description = "用户姓名(模糊查询)", example = "张")
    private String realName;

    @Schema(description = "用户状态(0:启用,1:禁用)", example = "0")
    private String status;

    @Schema(description = "用户角色(0:普通用户,1:管理员)", example = "0")
    private String role;

    @Schema(description = "页码(从1开始)", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
