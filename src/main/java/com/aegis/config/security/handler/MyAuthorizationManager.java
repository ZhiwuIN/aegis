package com.aegis.config.security.handler;

import com.aegis.common.constant.CommonConstants;
import com.aegis.config.security.customize.SecurityMetadataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/24 9:59
 * @Description: 自定义授权管理器
 */
@Component
@RequiredArgsConstructor
public class MyAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final SecurityMetadataService securityMetadataService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        // OPTIONS请求全部放行
        if (HttpMethod.OPTIONS.matches(method)) {
            return new AuthorizationDecision(true);
        }

        // 获取当前请求需要的角色
        Set<String> requiredRoles = securityMetadataService.getRequiredRoles(requestURI, method);

        // 如果是白名单，直接放行
        if (requiredRoles == null) {
            return new AuthorizationDecision(true);
        }

        // 获取认证信息
        Authentication auth = authentication.get();

        // 需要登录但不需要特定角色
        if (requiredRoles.contains(CommonConstants.NONE)) {
            return new AuthorizationDecision(true);
        }

        // 检查用户角色
        Set<String> userRoles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());


        return new AuthorizationDecision(requiredRoles.stream()
                .anyMatch(userRoles::contains));
    }
}
