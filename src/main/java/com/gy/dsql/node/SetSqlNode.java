package com.gy.dsql.node;

import java.util.Arrays;


public class SetSqlNode extends TrimSqlNode {

    public SetSqlNode(SqlNode contents) {
        super(contents, "SET ", null, null, Arrays.asList(","));
    }
}
