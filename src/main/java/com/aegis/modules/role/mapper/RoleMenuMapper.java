package com.aegis.modules.role.mapper;

import com.aegis.modules.role.domain.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025-08-30 10:48:33
 * @Description: 针对表【t_role_menu(角色和菜单关联表)】的数据库操作Mapper
 * @Entity: com.aegis.modules.role.domain.entity.RoleMenu
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    void batchRoleMenu(List<RoleMenu> roleMenus);

    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") Integer menuCheckStrictly);
}




