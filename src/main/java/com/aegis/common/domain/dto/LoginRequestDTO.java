package com.aegis.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/2 15:51
 * @Description: 登录方式DTO
 */
@Data
@ApiModel("登录方式DTO")
public class LoginRequestDTO {

    /**
     * 登录方式,枚举值:password(账号密码登录),email(邮箱验证码登录),sms(短信登录)
     */
    @ApiModelProperty("登录方式,枚举值:password(账号密码登录),email(邮箱验证码登录),sms(短信登录)")
    private String loginType;
}
