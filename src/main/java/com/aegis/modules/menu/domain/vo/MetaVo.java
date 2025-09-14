package com.aegis.modules.menu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/14 11:02
 * @Description: 路由元数据VO
 */
@Data
@ApiModel("路由元数据VO")
@NoArgsConstructor
@AllArgsConstructor
public class MetaVo {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("是否缓存")
    private Boolean noCache;

    @ApiModelProperty("内链地址")
    private String link;

    public MetaVo(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public MetaVo(String title, String icon, String link) {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }
}
