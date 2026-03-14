package com.aegis.modules.phone.service;

import com.aegis.modules.phone.domain.dto.PhoneNumberDTO;
import com.aegis.modules.phone.domain.entity.PhoneNumber;
import com.aegis.modules.phone.domain.vo.PhoneNumberVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码转换
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PhoneNumberConvert {

    PhoneNumberVO toPhoneNumberVo(PhoneNumber entity);

    PhoneNumber toPhoneNumber(PhoneNumberDTO dto);
}
