package com.gy.dsql.node;

import com.gy.dsql.context.DynamicContext;

import java.util.List;
import java.util.Set;


public class MixedSqlNode implements SqlNode {

    List<SqlNode> contents ;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext context) {
        contents.forEach(node -> node.apply(context));
        return true;
    }

    @Override
    public void applyParameter(Set<String> set) {
        for (SqlNode node: contents){
            node.applyParameter(set);
        }
    }
}
