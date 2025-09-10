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
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

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
            Select select = (Select) CCJSqlParserUtil.parse(originalSql);

            if (select instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) select;
                Expression dataPermissionExpression = buildDataPermissionExpression(plainSelect);

                if (dataPermissionExpression != null) {
                    Expression where = plainSelect.getWhere();
                    if (where == null) {
                        plainSelect.setWhere(dataPermissionExpression);
                    } else {
                        plainSelect.setWhere(new AndExpression(where, dataPermissionExpression));
                    }

                    PluginUtils.mpBoundSql(boundSql).sql(select.toString());
                }
            }
        } catch (Exception e) {
            log.error("Failed to apply data permission for SQL: {}", originalSql, e);
        }
    }

    private Expression buildDataPermissionExpression(PlainSelect plainSelect) {
        DataPermissionHandler dataPermissionHandler = SpringContextUtil.getBean(DataPermissionHandler.class);
        DataPermissionContext context = dataPermissionHandler.getCurrentUserDataPermission();
        if (context == null || context.getDataScope() == null) {
            return null;
        }

        String dataScope = context.getDataScope();
        Long userId = context.getUserId();
        Long deptId = context.getDeptId();
        Set<Long> deptIds = context.getDeptIds();

        Table table = (Table) plainSelect.getFromItem();
        String tableName = table.getName();

        switch (dataScope) {

            case DataScopeConstants.DATA_SCOPE_CUSTOM:
                if (deptIds != null && !deptIds.isEmpty()) {
                    return buildDeptInExpression(tableName, deptIds);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_DEPT:
                if (deptId != null) {
                    return buildDeptEqualsExpression(tableName, deptId);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD:
                Set<Long> allDeptIds = dataPermissionHandler.getDeptAndChildrenIds(deptId);
                if (!allDeptIds.isEmpty()) {
                    return buildDeptInExpression(tableName, allDeptIds);
                }
                return null;

            case DataScopeConstants.DATA_SCOPE_SELF:
                return buildUserEqualsExpression(tableName, userId);

            default:
                return null;
        }
    }

    private Expression buildDeptEqualsExpression(String tableName, Long deptId) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(tableName + ".dept_id"));
        equalsTo.setRightExpression(new LongValue(deptId));
        return equalsTo;
    }

    private Expression buildDeptInExpression(String tableName, Set<Long> deptIds) {
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(tableName + ".dept_id"));

        List<Expression> expressions = new ArrayList<>();
        for (Long deptId : deptIds) {
            expressions.add(new LongValue(deptId));
        }

        ExpressionList<Expression> expressionList = new ExpressionList<>(expressions);
        inExpression.setRightExpression(expressionList);

        return inExpression;
    }

    private Expression buildUserEqualsExpression(String tableName, Long userId) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(tableName + ".create_by"));
        equalsTo.setRightExpression(new LongValue(userId));
        return equalsTo;
    }

    private DataPermission getDataPermissionAnnotation(String mapperId) {
        try {
            String className = mapperId.substring(0, mapperId.lastIndexOf("."));
            String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
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
