package com.aegis.modules.menu.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.TreeVO;
import com.aegis.common.event.DataChangePublisher;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.menu.domain.dto.MenuDTO;
import com.aegis.modules.menu.domain.entity.Menu;
import com.aegis.modules.menu.mapper.MenuMapper;
import com.aegis.modules.menu.service.MenuConvert;
import com.aegis.modules.menu.service.MenuService;
import com.aegis.modules.role.domain.entity.RoleMenu;
import com.aegis.modules.role.mapper.RoleMenuMapper;
import com.aegis.utils.SecurityUtils;
import com.aegis.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:12
 * @Description: 菜单业务实现层
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    private final RoleMenuMapper roleMenuMapper;

    private final DataChangePublisher dataChangePublisher;

    private final MenuConvert menuConvert;

    @Override
    public List<Menu> list(MenuDTO dto) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dto.getMenuName()), Menu::getMenuName, dto.getMenuName())
                .eq(StringUtils.isNotBlank(dto.getStatus()), Menu::getStatus, dto.getStatus())
                .orderBy(true, true, Menu::getParentId, Menu::getOrderNum);
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public Menu detail(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public String delete(Long id) {
        LambdaQueryWrapper<Menu> childMenu = new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, id);
        if (menuMapper.selectCount(childMenu) > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }

        LambdaQueryWrapper<RoleMenu> roleMenu = new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, id);

        if (roleMenuMapper.selectCount(roleMenu) > 0) {
            throw new BusinessException("菜单已分配，不允许删除");
        }

        menuMapper.deleteById(id);

        dataChangePublisher.publishMenuChange("删除菜单,ID: " + id);

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public String add(MenuDTO dto) {
        Menu menu = menuConvert.toMenu(dto);

        checkSameMune(menu);

        menu.setCreateBy(SecurityUtils.getUserId());
        menuMapper.insert(menu);

        dataChangePublisher.publishMenuChange("新增菜单");

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public String update(MenuDTO dto) {
        Menu menu = menuConvert.toMenu(dto);

        checkSameMune(menu);

        if (dto.getParentId().equals(dto.getId())) {
            throw new BusinessException("修改菜单'" + dto.getMenuName() + "'失败,上级菜单不能选择自己");
        }

        menu.setUpdateBy(SecurityUtils.getUserId());

        menuMapper.updateById(menu);

        dataChangePublisher.publishMenuChange("修改菜单,ID: " + dto.getId());

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public List<TreeVO> tree(MenuDTO dto) {
        List<Menu> menuList = list(dto);

        List<Menu> menuTree = TreeUtil.makeTree(
                menuList,
                Menu::getParentId,
                Menu::getId,
                dept -> dept.getParentId() == null || dept.getParentId() == 0L,
                Menu::setChildren);

        return menuTree.stream().map(TreeVO::new).collect(Collectors.toList());
    }

    private void checkSameMune(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getMenuName, menu.getMenuName())
                .eq(Menu::getParentId, menu.getParentId())
                .ne(ObjectUtils.isNotEmpty(menu.getId()), Menu::getId, menu.getId());
        if (menuMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("同一层级下存在相同名称的菜单");
        }
    }
}
