package com.aegis.modules.phone.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码VO
 */
@Data
@Schema(description = "手机号码VO")
public class PhoneNumberVO {

    @Schema(description = "主键ID")
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "归属用户ID")
    private Long ownerUserId;

    @Schema(description = "归属用户名")
    private String ownerUsername;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建人名称")
    private String createByName;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新人名称")
    private String updateByName;

    @Schema(description = "所属项目")
    private String projectName;

    @Schema(description = "等级")
    private String level;
}
