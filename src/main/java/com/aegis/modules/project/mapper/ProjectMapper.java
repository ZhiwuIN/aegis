package com.aegis.modules.project.mapper;

import com.aegis.modules.phone.domain.entity.PhoneNumber;
import com.aegis.modules.project.domain.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目信息Mapper
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
