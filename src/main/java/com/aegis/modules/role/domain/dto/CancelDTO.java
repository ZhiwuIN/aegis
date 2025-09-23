package com.aegis.modules.role.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/13 18:41
 * @Description: 角色取消授权用户DTO
 */
@Data
@Schema(description = "角色取消授权用户DTO")
public class CancelDTO {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID")
    private Long roleId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;
}
