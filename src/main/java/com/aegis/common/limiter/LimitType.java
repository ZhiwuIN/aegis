package com.aegis.common.limiter;


/**
 * @Author: xuesong.lei
 * @Date: 2025/09/19 10:29
 * @Description: 限流类型
 */
public enum LimitType {

    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP
}
