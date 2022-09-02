package com.gy.dsql.node;

import com.gy.dsql.context.DynamicContext;

import java.util.List;
import java.util.Set;

/**
 * @author guoyang
 * @date 2022/9/2 8:27 下午
 */
public class ChooseSqlNode implements SqlNode {

    private final SqlNode defaultSqlNode;
    private final List<SqlNode> ifSqlNodes;

    public ChooseSqlNode(List<SqlNode> ifSqlNodes, SqlNode defaultSqlNode) {
        this.ifSqlNodes = ifSqlNodes;
        this.defaultSqlNode = defaultSqlNode;
    }

    @Override
    public boolean apply(DynamicContext context) {
        for (SqlNode sqlNode : ifSqlNodes) {
            if (sqlNode.apply(context)) {
                return true;
            }
        }
        if (defaultSqlNode != null) {
            defaultSqlNode.apply(context);
            return true;
        }
        return false;
    }

    @Override
    public void applyParameter(Set<String> set) {

    }
}
