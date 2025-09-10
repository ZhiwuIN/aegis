package com.aegis.common.datascope;

import lombok.Data;

import java.util.Set;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/10 14:08
 * @Description: 数据权限上下文
 */
@Data
public class DataPermissionContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 数据范围
     */
    private String dataScope;

    /**
     * 自定义部门ID集合
     */
    private Set<Long> deptIds;
}
