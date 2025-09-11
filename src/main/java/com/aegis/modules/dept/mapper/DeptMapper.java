package com.aegis.modules.dept.mapper;

import com.aegis.modules.dept.domain.entity.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: xuesong.lei
 * @Date: 2025-08-30 10:44:18
 * @Description: 针对表【t_dept(部门信息表)】的数据库操作Mapper
 * @Entity: com.aegis.modules.dept.domain.entity.Dept
 */
public interface DeptMapper extends BaseMapper<Dept> {

    Set<Long> getDeptAndChildrenIds(Long deptId);

    void updateBatchAncestors(@Param("children")List<Dept> children);
}




