package com.aegis.modules.dept.domain.dto;

import com.aegis.common.validator.ValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/9 17:56
 * @Description: 部门DTO
 */
@Data
@ApiModel("部门DTO")
public class DeptDTO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("父部门ID")
    @NotNull(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "上级部门不能为空")
    private Long parentId;

    @ApiModelProperty("部门名称")
    @NotBlank(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "部门名称不能为空")
    private String deptName;

    @ApiModelProperty("显示顺序")
    @NotNull(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "显示顺序不能为空")
    private Integer orderNum;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("部门状态")
    private String status;
}
