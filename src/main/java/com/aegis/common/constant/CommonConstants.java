package com.aegis.common.constant;

/**
 * @Author: xuesong.lei
 * @Date: 2025/08/21 15:08
 * @Description: 通用常量
 */
public class CommonConstants {

    /**
     * 访问令牌请求头
     */
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 用户代理请求头
     */
    public static final String USER_AGENT = "User-Agent";

    /**
     * 所有请求方式
     */
    public static final String REQUEST_METHOD_ALL = "all";

    /**
     * 正常/成功
     */
    public static final String NORMAL_STATUS = "0";

    /**
     * 停用/失败
     */
    public static final String DISABLE_STATUS = "1";

    /**
     * 菜单类型-目录
     */
    public static final String MENUS_CATALOG = "D";

    /**
     * 菜单类型-菜单
     */
    public static final String MENUS_MENU = "M";

    /**
     * 菜单类型-按钮
     */
    public static final String MENUS_BUTTON = "B";

    /**
     * 内链域名特殊字符替换
     */
    public static final String HTTP = "http://";

    /**
     * 内链域名特殊字符替换
     */
    public static final String HTTPS = "https://";

    /**
     * 内链域名特殊字符替换
     */
    public static final String WWW = "www.";

    /**
     * Layout组件标识
     */
    public final static String LAYOUT = "Layout";

    /**
     * ParentView组件标识
     */
    public final static String PARENT_VIEW = "ParentView";

    /**
     * InnerLink组件标识
     */
    public final static String INNER_LINK = "InnerLink";

    /**
     * 部门顶级id
     */
    public static final String DEPT_ANCESTOR_ID = "0";

    /**
     * 超级管理员角色编码
     */
    public static final String ADMIN_ROLE = "admin";

    /**
     * 无权限标识
     */
    public static final String NONE = "none";

    /**
     * 匿名用户标识
     */
    public static final String ANONYMOUS = "anonymous";

    /**
     * 操作成功消息
     */
    public static final String SUCCESS_MESSAGE = "操作成功";
}
