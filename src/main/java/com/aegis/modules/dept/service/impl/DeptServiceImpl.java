package com.aegis.modules.dept.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.TreeVO;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.dept.domain.dto.DeptDTO;
import com.aegis.modules.dept.domain.entity.Dept;
import com.aegis.modules.dept.mapper.DeptMapper;
import com.aegis.modules.dept.service.DeptConvert;
import com.aegis.modules.dept.service.DeptService;
import com.aegis.modules.user.domain.entity.User;
import com.aegis.modules.user.mapper.UserMapper;
import com.aegis.utils.SecurityUtils;
import com.aegis.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/9 14:28
 * @Description: 部门业务实现层
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    private final UserMapper userMapper;

    private final DeptConvert deptConvert;

    @Override
    public List<Dept> list(DeptDTO dto) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dto.getDeptName()), Dept::getDeptName, dto.getDeptName())
                .eq(StringUtils.isNotBlank(dto.getStatus()), Dept::getStatus, dto.getStatus())
                .orderBy(true, true, Dept::getParentId, Dept::getOrderNum);
        return deptMapper.selectList(queryWrapper);
    }

    @Override
    public Dept detail(Long id) {
        return deptMapper.selectById(id);
    }

    @Override
    public List<Dept> exclude(Long id) {
        List<Dept> deptList = deptMapper.selectList(new QueryWrapper<>());
        deptList.removeIf(item -> {
            if (item.getId() != null && item.getId().equals(id)) {
                return true;
            }
            String ancestors = item.getAncestors();
            return ancestors != null && Arrays.asList(ancestors.split(",")).contains(String.valueOf(id));
        });
        return deptList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        LambdaQueryWrapper<Dept> childDept = new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, id);
        if (deptMapper.selectCount(childDept) > 0) {
            throw new BusinessException("存在下级部门,不允许删除");
        }

        LambdaQueryWrapper<User> userDept = new LambdaQueryWrapper<User>()
                .eq(User::getDeptId, id);
        if (userMapper.selectCount(userDept) > 0) {
            throw new BusinessException("部门存在用户,不允许删除");
        }

        deptMapper.deleteById(id);

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(DeptDTO dto) {
        Dept dept = deptConvert.toDept(dto);

        // 只能同一层级下存在相同名称的部门
        checkSameDept(dept);

        Dept parentDept = deptMapper.selectById(dept.getParentId());
        if (CommonConstants.DISABLE_STATUS.equals(parentDept.getStatus())) {
            throw new BusinessException("部门停用，不允许新增");
        }

        dept.setCreateBy(SecurityUtils.getUserId());
        dept.setAncestors(parentDept.getAncestors() + CommonConstants.COMMA + dept.getParentId());
        deptMapper.insert(dept);

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String update(DeptDTO dto) {
        Dept dept = deptConvert.toDept(dto);

        // 只能同一层级下存在相同名称的部门
        checkSameDept(dept);

        if (dept.getParentId().equals(dept.getId())) {
            throw new BusinessException("修改部门'" + dept.getDeptName() + "'失败,上级部门不能是自己");
        }

        if (CommonConstants.DISABLE_STATUS.equals(dept.getStatus())
                && deptMapper.selectCount(new LambdaQueryWrapper<Dept>()
                .apply("FIND_IN_SET({0}, ancestors)", dept.getId())
                .eq(Dept::getStatus, CommonConstants.NORMAL_STATUS)) > 0) {
            throw new BusinessException("该部门包含未停用的子部门");
        }

        Dept newParent = deptMapper.selectById(dept.getParentId());
        Dept oldDept = deptMapper.selectById(dept.getId());
        if (ObjectUtils.isNotEmpty(newParent) && ObjectUtils.isNotEmpty(oldDept)) {
            String newAncestors = newParent.getAncestors() + CommonConstants.COMMA + newParent.getId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            // 更新下属部门的所有Ancestors
            updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
        }

        dept.setUpdateBy(SecurityUtils.getUserId());

        deptMapper.updateById(dept);

        if (CommonConstants.NORMAL_STATUS.equals(dept.getStatus()) && StrUtil.isNotEmpty(dept.getAncestors()) && !CommonConstants.DEPT_ANCESTOR_ID.equals(dept.getAncestors())) {
            // 如果该部门是启动状态，则启动该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public List<TreeVO> tree() {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getStatus, CommonConstants.NORMAL_STATUS);
        List<Dept> deptList = deptMapper.selectList(queryWrapper);

        List<Dept> deptTree = TreeUtil.makeTree(
                deptList,
                Dept::getParentId,
                Dept::getId,
                dept -> dept.getParentId() == null || dept.getParentId() == 0L,
                Dept::setChildren);

        return deptTree.stream().map(TreeVO::new).collect(Collectors.toList());
    }

    private void checkSameDept(Dept dept) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getDeptName, dept.getDeptName())
                .eq(Dept::getParentId, dept.getParentId())
                .ne(ObjectUtils.isNotEmpty(dept.getId()), Dept::getId, dept.getId());
        if (deptMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("同一层级下存在相同名称的部门");
        }
    }

    private void updateDeptChildren(Long id, String newAncestors, String oldAncestors) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.apply("FIND_IN_SET({0}, ancestors)", id);

        List<Dept> children = deptMapper.selectList(queryWrapper);
        for (Dept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }

        if (!children.isEmpty()) {
            deptMapper.updateBatchAncestors(children);
        }
    }

    private void updateParentDeptStatusNormal(Dept dept) {
        String ancestors = dept.getAncestors();
        List<Long> deptIds = Arrays.stream(ancestors.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        LambdaUpdateWrapper<Dept> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dept::getStatus, CommonConstants.NORMAL_STATUS).in(Dept::getId, deptIds);
        deptMapper.update(updateWrapper);
    }
}
