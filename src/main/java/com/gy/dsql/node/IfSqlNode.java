package com.gy.dsql.node;

import com.gy.dsql.context.DynamicContext;
import java.util.Set;

public class IfSqlNode implements SqlNode {
    String test;
    SqlNode contents;
    public IfSqlNode(String test, SqlNode contents) {
        this.test = test;
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext context) {
        Boolean value = context.getOgnlBooleanValue(test);
        if (value) {
            context.appendSql(" ");//标签类SqlNode先拼接空格，和前面的内容隔开
            this.contents.apply(context);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void applyParameter(Set<String> set) {
        contents.applyParameter(set);
    }
}
