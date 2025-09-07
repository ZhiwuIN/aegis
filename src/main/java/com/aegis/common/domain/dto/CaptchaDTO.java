package com.aegis.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/2 21:25
 * @Description: 滑块验证码DTO
 */
@Data
@ApiModel("滑块验证码DTO")
public class CaptchaDTO {

    /**
     * 验证码key
     */
    @ApiModelProperty("验证码key")
    private String captchaKey;

    /**
     * 滑块X轴位置
     */
    @ApiModelProperty("滑块X轴位置")
    private Integer slideX;
}
