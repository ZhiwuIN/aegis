package com.aegis.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/2 15:51
 * @Description: 邮箱验证码DTO
 */
@Data
@ApiModel("邮箱验证码DTO")
@EqualsAndHashCode(callSuper = true)
public class EmailLoginRequestDTO extends LoginRequestDTO {

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    private String code;
}
