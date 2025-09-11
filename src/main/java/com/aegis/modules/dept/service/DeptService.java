package com.aegis.modules.dept.service;

import com.aegis.common.domain.vo.TreeVO;
import com.aegis.modules.dept.domain.dto.DeptDTO;
import com.aegis.modules.dept.domain.entity.Dept;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/9 14:28
 * @Description: 部门业务层
 */
public interface DeptService {

    /**
     * 列表
     *
     * @param dto 查询参数
     * @return 部门列表
     */
    List<Dept> list(DeptDTO dto);

    /**
     * 详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    Dept detail(Long id);

    /**
     * 查询部门列表(排除查询节点)
     *
     * @param id 部门ID
     * @return 部门列表
     */
    List<Dept> exclude(Long id);

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 响应消息
     */
    String delete(Long id);

    /**
     * 新增或修改部门
     *
     * @param dto 部门DTO
     * @return 响应消息
     */
    String addOrUpdate(DeptDTO dto);

    /**
     * 部门树形结构
     *
     * @return 树形结构列表
     */
    List<TreeVO> tree();
}
