package com.aegis.modules.role.service.impl;

import com.aegis.modules.role.mapper.RoleMapper;
import com.aegis.modules.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:07
 * @Description: 角色业务实现层
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;


}
