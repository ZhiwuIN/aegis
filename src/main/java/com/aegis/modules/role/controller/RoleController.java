package com.aegis.modules.role.controller;

import com.aegis.modules.role.service.RoleService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:08
 * @Description: 角色接口
 */
@RestController
@Api(tags = "用户接口")
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
}
