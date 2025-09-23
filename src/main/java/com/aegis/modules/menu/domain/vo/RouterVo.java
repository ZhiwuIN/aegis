package com.aegis.modules.menu.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/14 11:00
 * @Description: 前端路由VO
 */
@Data
@Schema(description = "前端路由VO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "是否隐藏路由,当设置 true 的时候该路由不会再侧边栏出现")
    private Boolean hidden;

    @Schema(description = "重定向地址,当设置 noRedirect 的时候该路由在面包屑导航中不可被点击")
    private String redirect;

    @Schema(description = "组件地址")
    private String component;

    @Schema(description = "当你一个路由下面的 children 声明的路由大于1个时,自动会变成嵌套的模式")
    private Boolean alwaysShow;

    @Schema(description = "其他元素")
    private MetaVo meta;

    @Schema(description = "子路由")
    private List<RouterVo> children;
}
