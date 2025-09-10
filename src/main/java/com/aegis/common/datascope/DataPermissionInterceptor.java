package com.aegis.common.datascope;

import com.aegis.common.constant.CommonConstants;
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
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/10 14:08
 * @Description: 数据权限拦截器
 */
@Slf4j
public class DataPermissionInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (!SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
            return;
        }

        DataPermission dataPermission = getDataPermissionAnnotation(ms.getId());
        if (dataPermission != null && !dataPermission.enable()) {
            return;
        }

        String originalSql = boundSql.getSql();

        try {
            PlainSelect plainSelect = (PlainSelect) CCJSqlParserUtil.parse(originalSql);

            Expression dataPermissionExpression = buildDataPermissionExpression(plainSelect, dataPermission);

            if (dataPermissionExpression != null) {
                Expression where = plainSelect.getWhere();
                if (where == null) {
                    plainSelect.setWhere(dataPermissionExpression);
                } else {
                    plainSelect.setWhere(new AndExpression(where, dataPermissionExpression));
                }

                PluginUtils.mpBoundSql(boundSql).sql(plainSelect.toString());
                log.debug("Applied data permission filter: {}", plainSelect.toString());
            }
        } catch (Exception e) {
            log.error("Failed to apply data permission for SQL: {}", originalSql, e);
        }
    }

    private Expression buildDataPermissionExpression(PlainSelect plainSelect, DataPermission dataPermission) {
        DataPermissionHandler dataPermissionHandler = SpringContextUtil.getBean(DataPermissionHandler.class);
        DataPermissionContext context = dataPermissionHandler.getCurrentUserDataPermission();

        if (context == null || context.getDataScope() == null) {
            return null;
        }

        String dataScope = context.getDataScope();

        // 全部数据权限，不添加任何过滤条件
        if (DataScopeConstants.DATA_SCOPE_ALL.equals(dataScope)) {
            return null;
        }

        String tablePrefix = getTablePrefix(plainSelect, dataPermission);
        String deptField = getDeptField(dataPermission);
        String userField = getUserField(dataPermission);

        switch (dataScope) {
            case DataScopeConstants.DATA_SCOPE_CUSTOM:
                Set<Long> deptIds = context.getDeptIds();
                if (deptIds != null && !deptIds.isEmpty()) {
                    return buildDeptInExpression(tablePrefix, deptField, deptIds);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_DEPT:
                Long deptId = context.getDeptId();
                if (deptId != null) {
                    return buildDeptEqualsExpression(tablePrefix, deptField, deptId);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD:
                Set<Long> allDeptIds = dataPermissionHandler.getDeptAndChildrenIds(context.getDeptId());
                if (allDeptIds != null && !allDeptIds.isEmpty()) {
                    return buildDeptInExpression(tablePrefix, deptField, allDeptIds);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_SELF:
                return buildUserEqualsExpression(tablePrefix, userField, context.getUserId());

            default:
                return null;
        }
    }

    private String getTablePrefix(PlainSelect plainSelect, DataPermission dataPermission) {
        // 优先使用注解中指定的表别名
        if (dataPermission != null && StringUtils.hasText(dataPermission.tableAlias())) {
            return dataPermission.tableAlias();
        }

        // 获取主表信息
        Table table = (Table) plainSelect.getFromItem();
        String tableName = table.getName();

        // 如果有别名，使用别名；否则使用表名
        return table.getAlias() != null ? table.getAlias().getName() : tableName;
    }

    private String getDeptField(DataPermission dataPermission) {
        return dataPermission != null && StringUtils.hasText(dataPermission.deptField())
                ? dataPermission.deptField() : DataScopeConstants.DEFAULT_DEPT_FIELD;
    }

    private String getUserField(DataPermission dataPermission) {
        return dataPermission != null && StringUtils.hasText(dataPermission.userField())
                ? dataPermission.userField() : DataScopeConstants.DEFAULT_USER_FIELD;
    }

    private Expression buildDeptEqualsExpression(String tablePrefix, String deptField, Long deptId) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(tablePrefix + CommonConstants.POINT + deptField));
        equalsTo.setRightExpression(new LongValue(deptId));
        return equalsTo;
    }

    private Expression buildDeptInExpression(String tablePrefix, String deptField, Set<Long> deptIds) {
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(tablePrefix + CommonConstants.POINT + deptField));

        List<Expression> expressions = new ArrayList<>();
        for (Long deptId : deptIds) {
            expressions.add(new LongValue(deptId));
        }

        ExpressionList<Expression> expressionList = new ExpressionList<>(expressions);
        inExpression.setRightExpression(expressionList);
        return inExpression;
    }

    private Expression buildUserEqualsExpression(String tablePrefix, String userField, Long userId) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(tablePrefix + CommonConstants.POINT + userField));
        equalsTo.setRightExpression(new LongValue(userId));
        return equalsTo;
    }

    private DataPermission getDataPermissionAnnotation(String mapperId) {
        try {
            String className = mapperId.substring(0, mapperId.lastIndexOf(CommonConstants.POINT));
            String methodName = mapperId.substring(mapperId.lastIndexOf(CommonConstants.POINT) + 1);
            Class<?> clazz = Class.forName(className);

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    DataPermission annotation = method.getAnnotation(DataPermission.class);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }

            return clazz.getAnnotation(DataPermission.class);
        } catch (Exception e) {
            log.debug("Failed to get DataPermission annotation for: {}", mapperId, e);
            return null;
        }
    }
}
