package com.aegis.modules.role.domain.dto;

import com.aegis.common.domain.dto.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/13 18:41
 * @Description: 用户和角色DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户和角色DTO")
public class UserAndRoleQueryDTO extends PageDTO {

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("电话")
    private String phone;
}
