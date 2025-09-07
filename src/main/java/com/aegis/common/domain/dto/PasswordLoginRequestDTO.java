package com.aegis.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/2 21:26
 * @Description: 用户名密码DTO
 */
@Data
@ApiModel("用户名密码DTO")
@EqualsAndHashCode(callSuper = true)
public class PasswordLoginRequestDTO extends LoginRequestDTO {

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;
}
