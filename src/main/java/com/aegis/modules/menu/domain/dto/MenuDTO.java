package com.aegis.modules.menu.domain.dto;

import com.aegis.common.validator.ConditionalRequiredFields;
import com.aegis.common.validator.EnumString;
import com.aegis.common.validator.ValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/13 14:40
 * @Description: 菜单DTO
 */
@Data
@ApiModel("菜单DTO")
@ConditionalRequiredFields.List({
        @ConditionalRequiredFields(groups = {ValidGroup.Create.class, ValidGroup.Update.class},
                field = "menuType", value = "D", requiredFields = {"icon", "orderNum", "menuName", "path"}),// 当菜单类型为目录时，图标、显示顺序、菜单名称、路由地址必填
        @ConditionalRequiredFields(groups = {ValidGroup.Create.class, ValidGroup.Update.class},
                field = "menuType", value = "M", requiredFields = {"icon", "orderNum", "menuName", "path"}),// 当菜单类型为菜单时，图标、显示顺序、菜单名称、路由地址必填
        @ConditionalRequiredFields(groups = {ValidGroup.Create.class, ValidGroup.Update.class},
                field = "menuType", value = "B", requiredFields = {"orderNum", "menuName", "requestMethod", "requestUri", "perms"})// 当菜单类型为按钮时，显示顺序、菜单名称、请求方法、请求地址、权限标识必填
})
public class MenuDTO {

    @ApiModelProperty("主键ID")
    @Null(groups = ValidGroup.Create.class, message = "应用ID必须为空")
    @NotNull(groups = ValidGroup.Update.class, message = "应用ID不能为空")
    private Long id;

    @ApiModelProperty("菜单名称")
    @NotBlank(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "菜单名称不能为空")
    @Size(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    @ApiModelProperty("父菜单ID")
    @NotNull(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "父菜单ID不能为空")
    private Long parentId;

    @ApiModelProperty("显示顺序")
    @NotNull(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "显示顺序不能为空")
    private Integer orderNum;

    @ApiModelProperty("请求方法,GET,POST,PUT,DELETE,ALL=不限制")
    @EnumString(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, value = {"GET", "POST", "PUT", "DELETE", "ALL"}, message = "请求方法参数错误")
    private String requestMethod;

    @ApiModelProperty("URL匹配模式,支持Ant风格,比如/api/user/**")
    private String requestUri;

    @ApiModelProperty("路由名称")
    private String name;

    @ApiModelProperty("路由地址")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("是否为外链(0-否,1-是)")
    private Boolean isFrame;

    @ApiModelProperty("是否缓存(0-缓存,1-不缓存)")
    private Boolean keepAlive;

    @ApiModelProperty("菜单类型(D-目录,M-菜单,B-按钮)")
    @NotBlank(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "菜单类型不能为空")
    @EnumString(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, value = {"D", "M", "B"}, message = "菜单类型参数错误")
    private String menuType;

    @ApiModelProperty("菜单状态(0-显示,1-隐藏)")
    private Boolean hidden;

    @ApiModelProperty("菜单状态(0-正常,1-停用)")
    @EnumString(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, value = {"0", "1"}, message = "状态只允许为0或1")
    private String status;

    @ApiModelProperty("权限标识")
    private String perms;

    @ApiModelProperty("菜单图标")
    private String icon;
}
