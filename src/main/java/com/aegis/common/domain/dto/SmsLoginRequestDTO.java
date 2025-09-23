package com.aegis.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/2 22:11
 * @Description: 短信登录DTO
 */
@Data
@Schema(description = "短信登录DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsLoginRequestDTO {

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
}
