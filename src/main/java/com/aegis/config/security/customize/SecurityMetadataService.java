package com.aegis.config.security.customize;


import com.aegis.common.constant.CommonConstants;
import com.aegis.common.constant.RedisConstants;
import com.aegis.common.event.DataChangeListener;
import com.aegis.modules.resource.domain.entity.Resource;
import com.aegis.modules.resource.mapper.ResourceMapper;
import com.aegis.modules.whitelist.domain.entity.Whitelist;
import com.aegis.modules.whitelist.mapper.WhitelistMapper;
import com.aegis.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/24 10:04
 * @Description: 安全元数据服务，提供URL权限映射
 */
@Component
@RequiredArgsConstructor
public class SecurityMetadataService {

    private final ResourceMapper resourceMapper;

    private final WhitelistMapper whitelistMapper;

    private final RedisUtils redisUtils;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @PostConstruct
    public void init() {
        // 系统启动时预加载资源数据
        loadDataSourceAllResource();
        // 系统启动时预加载白名单数据
        loadDataSourceAllWhitelist();
    }

    /**
     * 获取指定URL和HTTP方法需要的角色
     */
    public Set<String> getRequiredRoles(String requestURI, String method) {
        // 判断请求路径是否在白名单内,在白名单内直接放行
        if (isWhitelisted(requestURI, method)) {
            return null;// 白名单直接放行
        }

        // 从 t_resource 中匹配 (request_method + request_uri)，获取 perm_code
        List<Resource> allResource = loadDataSourceAllResource();
        for (Resource resource : allResource) {
            if (matchesRequestMethod(resource.getRequestMethod(), method) &&
                    antPathMatcher.match(resource.getRequestUri(), requestURI)) {
                return Set.of(resource.getPermCode());
            }
        }

        // 默认需要认证但无特定角色要求
        return Set.of(CommonConstants.NONE);
    }

    /**
     * 检查URL是否在白名单中
     */
    private boolean isWhitelisted(String requestURI, String method) {
        List<Whitelist> whitelists = loadDataSourceAllWhitelist();
        return whitelists.stream()
                .anyMatch(whitelist ->
                        matchesRequestMethod(whitelist.getRequestMethod(), method) &&
                                antPathMatcher.match(whitelist.getRequestUri(), requestURI));
    }

    /**
     * 匹配HTTP方法
     */
    private boolean matchesRequestMethod(String configuredMethod, String requestMethod) {
        return CommonConstants.REQUEST_METHOD_ALL.equalsIgnoreCase(configuredMethod) ||
                configuredMethod.equalsIgnoreCase(requestMethod);
    }

    /**
     * 加载所有的资源存入Redis中
     * 在新增、修改、删除资源时,发布事件监听器{@link DataChangeListener},重新加载
     */
    public List<Resource> loadDataSourceAllResource() {
        if (redisUtils.hasKey(RedisConstants.RESOURCES)) {
            return redisUtils.getList(RedisConstants.RESOURCES, Resource.class);
        } else {
            List<Resource> allResource = resourceMapper.getAllResource();
            redisUtils.set(RedisConstants.RESOURCES, allResource, 1, TimeUnit.DAYS);
            return allResource;
        }
    }

    /**
     * 加载所有的白名单存入Redis中
     * 在新增、修改、删除白名单时,发布事件监听器{@link DataChangeListener},重新加载
     */
    public List<Whitelist> loadDataSourceAllWhitelist() {
        if (redisUtils.hasKey(RedisConstants.WHITELIST)) {
            return redisUtils.getList(RedisConstants.WHITELIST, Whitelist.class);
        } else {
            LambdaQueryWrapper<Whitelist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Whitelist::getStatus, CommonConstants.NORMAL_STATUS);
            List<Whitelist> rules = whitelistMapper.selectList(queryWrapper);
            redisUtils.set(RedisConstants.WHITELIST, rules, 1, TimeUnit.DAYS);
            return rules;
        }
    }
}
