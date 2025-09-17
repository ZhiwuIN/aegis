package com.aegis.modules.notice.domain.dto;

import com.aegis.common.domain.dto.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("通知标题")
    private String noticeTitle;

    @ApiModelProperty("通知类型(1=系统通知,2=公告,3=提醒)")
    private String noticeType;

    @ApiModelProperty("是否已读(0=未读,1=已读)")
    private Integer readFlag;

}
