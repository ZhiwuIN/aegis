package com.aegis.modules.common.service;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/3 15:18
 * @Description: 邮箱业务层
 */
public interface EmailService {

    /**
     * 发送注册验证码
     *
     * @param email 邮箱
     * @return 响应消息
     */
    String sendRegisterCode(String email);
}
