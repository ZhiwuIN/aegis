package com.aegis.modules.user.service;

import com.aegis.modules.user.domain.entity.User;
import com.aegis.modules.user.domain.vo.UserVO;
import org.mapstruct.Mapper;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/14 11:29
 * @Description: 用户信息转换类
 */
@Mapper(componentModel = "spring")
public interface UserConvert {

    UserVO toUserVo(User user);
}
