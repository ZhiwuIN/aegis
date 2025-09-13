package com.aegis.modules.role.mapper;

import com.aegis.modules.role.domain.dto.UserAndRoleQueryDTO;
import com.aegis.modules.role.domain.entity.Role;
import com.aegis.modules.user.domain.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: xuesong.lei
 * @Date: 2025-08-30 10:48:14
 * @Description: 针对表【t_role(角色信息表)】的数据库操作Mapper
 * @Entity: com.aegis.modules.role.domain.entity.Role
 */
public interface RoleMapper extends BaseMapper<Role> {

    Set<Long> getCustomDeptIds(Long id);

    String getHighestDataScope(Long id);

    List<UserVO> allocatedList(@Param("dto") UserAndRoleQueryDTO dto);

    List<UserVO> unallocatedList(@Param("dto") UserAndRoleQueryDTO dto);
}




