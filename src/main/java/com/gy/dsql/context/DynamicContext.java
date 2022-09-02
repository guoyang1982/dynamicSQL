package com.gy.dsql.context;


import com.gy.dsql.util.OgnlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DynamicContext {

    StringBuilder sqlBuilder = new StringBuilder();
    List<Object> jdbcParameters = new ArrayList<>();
    Set<String> paramNames = new HashSet<>();

    //    List<Object> jdbcParameterNames = new ArrayList<>();
    Map<String, Object> data;
    public Map<String,Object> bindings = new HashMap<>();

    public DynamicContext(Map<String, Object> data) {
        this.data = data;
    }

    public void appendSql(String text) {
        if (text != null)
            sqlBuilder.append(text);
    }

    public void addParameter(Object o) {
        jdbcParameters.add(o);
    }

    public void addParameterName(String o) {
        paramNames.add(o);
    }

    /**
     * 通过ognl表达式获取值
     *
     * @param expression
     * @return
     */
    public Object getOgnlValue(String expression) {
        return OgnlUtil.getValue(expression, data);
    }

    public Boolean getOgnlBooleanValue(String expression) {
        return OgnlUtil.getBooleanValue(expression, data);
    }

    public String getSql() {
        return sqlBuilder.toString();
    }

    public void setSql(String text) {
        sqlBuilder = new StringBuilder(text);
    }

    public List<Object> getJdbcParameters() {
        return jdbcParameters;
    }

    public Map<String, Object> getData() {
        return data;
    }

}
