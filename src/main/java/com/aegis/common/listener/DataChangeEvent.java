package com.aegis.common.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: xuesong.lei
 * @Date: 2025/08/21 14:44
 * @Description: 数据变更事件
 */
@Getter
@AllArgsConstructor
public class DataChangeEvent {

    public enum Type {
        MENU,       // 菜单变更
        WHITELIST,  // 白名单变更
        LOG         // 系统操作日志
    }

    /**
     * 事件类型
     */
    private final Type type;

    /**
     * 事件携带的具体数据
     */
    private final Object payload;

    /**
     * 事件描述信息
     */
    private final String description;
}
