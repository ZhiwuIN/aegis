package com.aegis.common.datascope;

import com.aegis.common.constant.DataScopeConstants;
import com.aegis.utils.SpringContextUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
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

        DataPermission dataPermission = getDataPermissionAnnotation(ms);
        // 没有注解或注解被禁用，则不处理
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

            Expression filterExpression = buildDataPermissionExpression(plainSelect, dataPermission);
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

            if (log.isTraceEnabled()) {
                log.trace("Applied data permission filter, SQL before:\n{}\nSQL after:\n{}", originalSql, newSql);
            }
        } catch (Exception e) {
            log.error("Failed to apply data permission filter for SQL: {}", originalSql, e);
        }
    }

    /**
     * 构建数据权限 SQL 条件
     */
    private Expression buildDataPermissionExpression(PlainSelect plainSelect, DataPermission dataPermission) {
        DataPermissionHandler handler = SpringContextUtil.getBean(DataPermissionHandler.class);
        DataPermissionContext context = handler.getCurrentUserDataPermission();

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

        log.warn("Unsupported SQL structure for data permission: {}", plainSelect);
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
    private Expression buildEqualsExpression(String tablePrefix, String field, Long value) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(buildColumn(tablePrefix, field));
        equalsTo.setRightExpression(new LongValue(value));
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

    private DataPermission getDataPermissionAnnotation(MappedStatement ms) {
        String mapperId = ms.getId();
        return annotationCache.computeIfAbsent(mapperId, id -> {
            try {
                // 先检查 Mapper 方法注解
                DataPermission mapperAnnotation = getMapperMethodAnnotation(id);
                if (mapperAnnotation != null) {
                    return mapperAnnotation;
                }

                // 检查调用栈中的 Service 方法注解
                return getServiceAnnotationFromCallStack();
            } catch (Exception e) {
                log.debug("Failed to get DataPermission annotation for: {}", mapperId, e);
                return null;
            }
        });
    }

    private DataPermission getMapperMethodAnnotation(String mapperId) {
        try {
            int lastDot = mapperId.lastIndexOf(".");
            String className = mapperId.substring(0, lastDot);
            String methodName = mapperId.substring(lastDot + 1);

            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(DataPermission.class)) {
                    return method.getAnnotation(DataPermission.class);
                }
            }
            return clazz.getAnnotation(DataPermission.class);
        } catch (Exception e) {
            return null;
        }
    }

    private DataPermission getServiceAnnotationFromCallStack() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            // 只检查 Service 层
            if (className.contains(".service.impl.") || className.endsWith("ServiceImpl")) {
                try {
                    Class<?> clazz = Class.forName(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.getName().equals(methodName) &&
                                method.isAnnotationPresent(DataPermission.class)) {
                            return method.getAnnotation(DataPermission.class);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
}
