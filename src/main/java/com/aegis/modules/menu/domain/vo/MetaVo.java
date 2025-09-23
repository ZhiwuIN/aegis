package com.aegis.modules.menu.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/14 11:02
 * @Description: 路由元数据VO
 */
@Data
@Schema(description = "路由元数据VO")
@NoArgsConstructor
@AllArgsConstructor
public class MetaVo {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "是否缓存")
    private Boolean noCache;

    @Schema(description = "内链地址")
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
