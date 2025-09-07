package com.aegis.modules.user.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/4 22:50
 * @Description: 用户业务层
 */
public interface UserService {

    /**
     * 刷新令牌
     *
     * @param request  请求
     * @param response 响应
     * @return 新的令牌
     */
    String refreshToken(HttpServletRequest request, HttpServletResponse response);
}
