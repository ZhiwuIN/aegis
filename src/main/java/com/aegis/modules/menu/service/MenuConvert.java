package com.aegis.modules.menu.service;

import com.aegis.modules.menu.domain.dto.MenuDTO;
import com.aegis.modules.menu.domain.entity.Menu;
import org.mapstruct.Mapper;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/13 16:20
 * @Description: 菜单类型转换类
 */
@Mapper(componentModel = "spring")
public interface MenuConvert {

    Menu toMenu(MenuDTO dto);
}
