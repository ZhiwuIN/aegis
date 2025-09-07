package com.aegis.modules.post.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xuesong.lei
 * @Date: 2025-08-30 10:48:04
 * @Description: 岗位信息表
 * @TableName t_post
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel("岗位信息表")
@TableName(value = "t_post")
public class Post implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 更新人
     */
    @ApiModelProperty("更新人")
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除标记(0=正常,1=删除)
     */
    @ApiModelProperty("逻辑删除标记(0=正常,1=删除)")
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 版本号,用于乐观锁
     */
    @ApiModelProperty("版本号,用于乐观锁")
    @Version
    @TableField(value = "version")
    private Integer version;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField(value = "remark")
    private String remark;

    /**
     * 岗位编码
     */
    @ApiModelProperty("岗位编码")
    @TableField(value = "post_code")
    private String postCode;

    /**
     * 岗位名称
     */
    @ApiModelProperty("岗位名称")
    @TableField(value = "post_name")
    private String postName;

    /**
     * 显示顺序
     */
    @ApiModelProperty("显示顺序")
    @TableField(value = "post_sort")
    private Integer postSort;

    /**
     * 状态(0-正常,1-停用)
     */
    @ApiModelProperty("状态(0-正常,1-停用)")
    @TableField(value = "status")
    private String status;

}
