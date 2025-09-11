package com.aegis.utils;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.exception.BusinessException;
import com.aegis.common.result.ResultCodeEnum;
import com.aegis.modules.user.domain.entity.User;
import com.aegis.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    public static Long getUserId() {
        User currentUser = getCurrentUser();
        return currentUser.getId();
    }

    public static User getCurrentUser() {
        UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
        String username = getUsername();
        if (CommonConstants.ANONYMOUS.equals(username)) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGGED_IN);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }
}
