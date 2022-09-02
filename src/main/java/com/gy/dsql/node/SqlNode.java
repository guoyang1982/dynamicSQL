package com.gy.dsql.node;


import com.gy.dsql.context.DynamicContext;

import java.util.Set;


public interface SqlNode {

    boolean apply(DynamicContext context);

    void applyParameter(Set<String> set);
}
