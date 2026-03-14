package com.aegis.modules.phone.mapper;

import com.aegis.modules.phone.domain.entity.PhoneNumber;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码Mapper
 */
@Mapper
public interface PhoneNumberMapper extends BaseMapper<PhoneNumber> {
}
