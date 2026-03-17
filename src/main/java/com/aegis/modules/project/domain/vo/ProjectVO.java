package com.aegis.modules.project.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "项目信息VO")
public class ProjectVO {

    @Schema(description = "主键ID")
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 项目管理员
     */
    @Schema(description = "项目管理员")
    private Long owner;

    /**
     * 项目管理员
     */
    @Schema(description = "项目管理员名称")
    private String ownerName;

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


}
