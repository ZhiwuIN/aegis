package com.aegis.common.datascope;

import java.lang.annotation.*;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/10 14:08
 * @Description: 数据权限注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 启用数据权限，默认为 true
     */
    boolean enable() default true;

    /**
     * 部门字段名，默认为 dept_id
     */
    String deptField() default "dept_id";

    /**
     * 用户字段名，默认为 create_by
     */
    String userField() default "create_by";

    /**
     * 复杂查询的表别名
     */
    String tableAlias() default "";
}
