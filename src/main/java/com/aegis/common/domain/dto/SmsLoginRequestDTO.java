package com.aegis.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/2 22:11
 * @Description: 短信登录DTO
 */
@Data
@ApiModel("短信登录DTO")
@EqualsAndHashCode(callSuper = true)
public class SmsLoginRequestDTO extends LoginRequestDTO {

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    private String code;
}
