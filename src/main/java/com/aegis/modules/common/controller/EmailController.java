package com.aegis.modules.common.controller;

import com.aegis.modules.common.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/3 15:14
 * @Description: 邮件接口
 */
@Api(tags = "邮件接口")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @ApiOperation("发送注册验证码")
    @GetMapping("/sendRegisterCode")
    public String sendRegisterCode(@RequestParam("email") String email) {
        return emailService.sendRegisterCode(email);
    }
}
