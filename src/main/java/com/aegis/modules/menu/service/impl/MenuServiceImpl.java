package com.aegis.modules.menu.service.impl;

import com.aegis.modules.menu.mapper.MenuMapper;
import com.aegis.modules.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:12
 * @Description: 菜单业务实现层
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

}
