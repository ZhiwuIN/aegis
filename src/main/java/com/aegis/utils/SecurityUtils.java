package com.aegis.utils;

import com.aegis.common.constant.CommonConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author: xuesong.lei
 * @Date: 2025/08/22 14:20
 * @Description: Security工具类
 */
public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return CommonConstants.ANONYMOUS;
        }
        return authentication.getName();
    }
}
