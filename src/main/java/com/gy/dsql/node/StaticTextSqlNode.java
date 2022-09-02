package com.gy.dsql.node;


import com.gy.dsql.context.DynamicContext;

import java.util.Set;

/**
 * @author guoyang
 * @date 2022/9/2 7:24 下午
 */
public class StaticTextSqlNode implements SqlNode {
    private final String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        context.appendSql(text);
        return true;
    }

    @Override
    public void applyParameter(Set<String> set) {

    }

}
