package com.aegis.modules.role.service;

import com.aegis.modules.role.domain.dto.RoleDTO;
import com.aegis.modules.role.domain.entity.Role;
import org.mapstruct.Mapper;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/13 17:24
 * @Description: 角色类型转换类
 */
@Mapper(componentModel = "spring")
public interface RoleConvert {

    Role toRole(RoleDTO dto);
}
