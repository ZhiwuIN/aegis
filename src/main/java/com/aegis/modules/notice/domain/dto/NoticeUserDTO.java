package com.aegis.modules.notice.domain.dto;

import com.aegis.common.domain.dto.PageDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/16 22:07
 * @Description: 通知用户DTO
 */
@Data
@ApiModel("通知用户DTO")
@EqualsAndHashCode(callSuper = true)
public class NoticeUserDTO extends PageDTO {
}
