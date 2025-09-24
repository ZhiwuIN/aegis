package com.aegis.config.security.customize;


import com.aegis.common.constant.CommonConstants;
import com.aegis.common.constant.RedisConstants;
import com.aegis.common.event.DataChangeListener;
import com.aegis.modules.menu.domain.entity.Menu;
import com.aegis.modules.menu.mapper.MenuMapper;
import com.aegis.modules.role.domain.entity.Role;
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
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/24 10:04
 * @Description: 安全元数据服务，提供URL权限映射
 */
@Component
@RequiredArgsConstructor
public class SecurityMetadataService {

    private final MenuMapper menuMapper;

    private final WhitelistMapper whitelistMapper;

    private final RedisUtils redisUtils;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @PostConstruct
    public void init() {
        // 系统启动时预加载菜单数据
        loadDataSourceAllUrl();
        // 系统启动时预加载白名单数据
        loadDataSourceAllWhitelist();
    }

    /**
     * 获取指定URL和HTTP方法需要的角色
     */
    public Set<String> getRequiredRoles(String requestURI, String method) {
        // 检查白名单
        if (isWhitelisted(requestURI, method)) {
            return null;// 白名单直接放行
        }

        // 检查菜单权限
        List<Menu> allMenu = loadDataSourceAllUrl();
        for (Menu menu : allMenu) {
            if (matchesRequestMethod(menu.getRequestMethod(), method) &&
                    antPathMatcher.match(menu.getRequestUri(), requestURI)) {
                return menu.getRoleList().stream()
                        .map(Role::getRoleCode)
                        .collect(Collectors.toSet());
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
     * 加载所有的URL存入Redis中
     * 在新增、修改、删除 【菜单、角色、角色关联菜单】时,发布事件监听器{@link DataChangeListener},重新加载
     */
    public List<Menu> loadDataSourceAllUrl() {
        if (redisUtils.hasKey(RedisConstants.MENUS)) {
            return redisUtils.getList(RedisConstants.MENUS, Menu.class);
        } else {
            List<Menu> allMenu = menuMapper.getAllMenu();
            redisUtils.set(RedisConstants.MENUS, allMenu, 1, TimeUnit.DAYS);
            return allMenu;
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
