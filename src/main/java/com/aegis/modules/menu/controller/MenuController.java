package com.aegis.modules.menu.controller;

import com.aegis.modules.menu.service.MenuService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:11
 * @Description: 菜单接口
 */
@RestController
@Api(tags = "菜单接口")
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
}
