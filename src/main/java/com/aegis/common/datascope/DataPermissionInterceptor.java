package com.aegis.common.datascope;

import com.aegis.common.constant.DataScopeConstants;
import com.aegis.utils.SpringContextUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/10 14:08
 * @Description: 数据权限拦截器
 */
@Slf4j
public class DataPermissionInterceptor implements InnerInterceptor {

    /**
     * 缓存 MapperId -> DataPermission 注解，减少反射调用
     */
    private final Map<String, DataPermission> annotationCache = new ConcurrentHashMap<>();

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {

        if (!SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
            return;
        }

        // 优先从线程本地变量获取数据权限信息（来自AOP切面）
        DataPermissionContextHolder.DataPermissionInfo permissionInfo = DataPermissionContextHolder.get();
        DataPermission dataPermission;
        DataPermissionContext context;

        if (permissionInfo != null) {
            // 来自AOP切面的权限信息
            dataPermission = permissionInfo.getAnnotation();
            context = permissionInfo.getContext();

            if (log.isTraceEnabled()) {
                log.trace("使用AOP切面提供的数据权限信息，MapperId: {}", ms.getId());
            }
        } else {
            // 回退到原有的注解检查方式（Mapper方法或类上的注解）
            dataPermission = getDataPermissionAnnotation(ms);
            if (dataPermission == null || !dataPermission.enable()) {
                return;
            }

            // 获取数据权限上下文
            DataPermissionHandler handler = SpringContextUtil.getBean(DataPermissionHandler.class);
            context = handler.getCurrentUserDataPermission();

            if (log.isTraceEnabled()) {
                log.trace("使用Mapper注解的数据权限信息，MapperId: {}", ms.getId());
            }
        }

        // 没有权限注解或注解被禁用，则不处理
        if (dataPermission == null || !dataPermission.enable()) {
            return;
        }

        String originalSql = boundSql.getSql();

        try {
            // 解析 SQL
            Statement statement = CCJSqlParserUtil.parse(originalSql);
            if (!(statement instanceof Select)) {
                return;
            }

            Select select = (Select) statement;
            if (select.getPlainSelect() == null) {
                return;
            }

            PlainSelect plainSelect = select.getPlainSelect();

            Expression filterExpression = buildDataPermissionExpression(plainSelect, dataPermission, context);
            if (filterExpression == null) {
                return;
            }

            // 合并 WHERE 条件
            if (plainSelect.getWhere() == null) {
                plainSelect.setWhere(filterExpression);
            } else {
                plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), filterExpression));
            }

            // 替换 SQL
            String newSql = plainSelect.toString();
            PluginUtils.mpBoundSql(boundSql).sql(newSql);

            if (log.isDebugEnabled()) {
                log.debug("应用数据权限过滤器，原SQL:\n{}\n新SQL:\n{}", originalSql, newSql);
            }
        } catch (Exception e) {
            log.error("应用数据权限过滤器失败，SQL: {}", originalSql, e);
        }
    }

    /**
     * 构建数据权限 SQL 条件
     */
    private Expression buildDataPermissionExpression(PlainSelect plainSelect,
                                                   DataPermission dataPermission,
                                                   DataPermissionContext context) {

        if (context == null || context.getDataScope() == null) {
            return null;
        }

        String dataScope = context.getDataScope();
        if (DataScopeConstants.DATA_SCOPE_ALL.equals(dataScope)) {
            return null; // 全部数据权限
        }

        String tablePrefix = getMainTableAlias(plainSelect, dataPermission);
        String deptField = getDeptField(dataPermission);
        String userField = getUserField(dataPermission);

        switch (dataScope) {
            case DataScopeConstants.DATA_SCOPE_CUSTOM:
                Set<Long> deptIds = context.getDeptIds();
                if (deptIds != null && !deptIds.isEmpty()) {
                    return buildInExpression(tablePrefix, deptField, deptIds);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_DEPT:
                Long deptId = context.getDeptId();
                if (deptId != null) {
                    return buildEqualsExpression(tablePrefix, deptField, deptId);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD:
                DataPermissionHandler handler = SpringContextUtil.getBean(DataPermissionHandler.class);
                Set<Long> allDeptIds = handler.getDeptAndChildrenIds(context.getDeptId());
                if (allDeptIds != null && !allDeptIds.isEmpty()) {
                    return buildInExpression(tablePrefix, deptField, allDeptIds);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_SELF:
                Long userId = context.getUserId();
                if (userId == null) {
                    return null;
                }
                return buildEqualsExpression(tablePrefix, userField, userId);

            default:
                return null;
        }
    }

    /**
     * 获取主表别名，如果注解指定则优先使用
     */
    private String getMainTableAlias(PlainSelect plainSelect, DataPermission dataPermission) {
        if (dataPermission != null && StringUtils.hasText(dataPermission.tableAlias())) {
            return dataPermission.tableAlias();
        }

        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            return table.getAlias() != null ? table.getAlias().getName() : table.getName();
        }

        log.warn("数据权限不支持的SQL结构: {}", plainSelect);
        return "";
    }

    private String getDeptField(DataPermission dataPermission) {
        return (dataPermission != null && StringUtils.hasText(dataPermission.deptField()))
                ? dataPermission.deptField()
                : DataScopeConstants.DEFAULT_DEPT_FIELD;
    }

    private String getUserField(DataPermission dataPermission) {
        return (dataPermission != null && StringUtils.hasText(dataPermission.userField()))
                ? dataPermission.userField()
                : DataScopeConstants.DEFAULT_USER_FIELD;
    }

    /**
     * 构建等于条件
     */
    private Expression buildEqualsExpression(String tablePrefix, String field, Object value) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(buildColumn(tablePrefix, field));
        if (value instanceof Number) {
            equalsTo.setRightExpression(new LongValue(value.toString()));
        } else {
            equalsTo.setRightExpression(new StringValue(value.toString()));
        }
        return equalsTo;
    }

    /**
     * 构建 IN 条件
     */
    private Expression buildInExpression(String tablePrefix, String field, Set<Long> values) {
        InExpression in = new InExpression();
        in.setLeftExpression(buildColumn(tablePrefix, field));

        List<Expression> list = values.stream().map(LongValue::new).collect(Collectors.toList());
        in.setRightExpression(new ExpressionList<>(list));
        return in;
    }

    /**
     * 使用 Column 避免 SQL 注入
     */
    private Column buildColumn(String tablePrefix, String field) {
        if (StringUtils.hasText(tablePrefix)) {
            return new Column(tablePrefix + "." + field);
        }
        return new Column(field);
    }

    /**
     * 从Mapper方法或类上获取DataPermission注解
     */
    private DataPermission getDataPermissionAnnotation(MappedStatement ms) {
        String mapperId = ms.getId();
        return annotationCache.computeIfAbsent(mapperId, id -> {
            try {
                int lastDot = id.lastIndexOf(".");
                String className = id.substring(0, lastDot);
                String methodName = id.substring(lastDot + 1);

                Class<?> clazz = Class.forName(className);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName)
                            && method.isAnnotationPresent(DataPermission.class)) {
                        return method.getAnnotation(DataPermission.class);
                    }
                }
                return clazz.getAnnotation(DataPermission.class);
            } catch (Exception e) {
                log.debug("获取DataPermission注解失败: {}", mapperId, e);
                return null;
            }
        });
    }
}
