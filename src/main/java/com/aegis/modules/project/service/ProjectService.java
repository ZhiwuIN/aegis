package com.aegis.modules.project.service;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.modules.project.domain.dto.ProjectDTO;
import com.aegis.modules.project.domain.vo.ProjectVO;

/**
 * 项目信息服务接口
 */
public interface ProjectService {

    /**
     * 分页列表
     *
     * 管理员：查看所有项目信息
     * 子用户：只查看自己创建的项目信息
     */
    PageVO<ProjectVO> pageList(ProjectDTO dto);

    /**
     * 详情
     */
    ProjectVO detail(Long id);

    /**
     * 新增项目信息
     */
    String add(ProjectDTO dto);

    /**
     * 修改项目信息
     */
    String update(ProjectDTO dto);

    /**
     * 删除项目信息
     */
    String delete(Long id);
}
