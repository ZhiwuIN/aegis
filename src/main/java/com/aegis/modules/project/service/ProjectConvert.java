package com.aegis.modules.project.service;

import com.aegis.modules.project.domain.dto.ProjectDTO;
import com.aegis.modules.project.domain.entity.Project;
import com.aegis.modules.project.domain.vo.ProjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码转换
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectConvert {

    ProjectVO toProjectVo(Project entity);

    Project toProject(ProjectDTO dto);
}
