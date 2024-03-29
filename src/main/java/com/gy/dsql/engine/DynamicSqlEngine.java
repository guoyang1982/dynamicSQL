package com.gy.dsql.engine;


import com.gy.dsql.SqlMeta;
import com.gy.dsql.context.DynamicContext;
import com.gy.dsql.node.SqlNode;
import com.gy.dsql.handler.XmlParser;
import com.gy.dsql.token.GenericTokenParser;
import com.gy.dsql.token.TokenHandler;
import com.gy.dsql.token.TokenParser;
import com.gy.dsql.token.VariableTokenHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class DynamicSqlEngine {

    Cache cache = new Cache();

    public SqlMeta parse(String text, Map<String, Object> params) {
        if (!text.startsWith("<script>")) {
            String sql = getTokenParserSql(text, params);
            return new SqlMeta(sql, null);
        }
//        Properties properties = new Properties();
//        properties.putAll(params);
//        Map<String, Object> map = new HashMap<String, Object>((Map) properties);
//        params = map;
        SqlNode sqlNode = parseXml2SqlNode(text);
        DynamicContext context = new DynamicContext(params);
        parseSqlText(sqlNode, context);
        parseParameter(context);
        SqlMeta sqlMeta = new SqlMeta(context.getSql(), context.getJdbcParameters());
        return sqlMeta;
    }

    private String getTokenParserSql(String text, Map<String, Object> params) {
        Properties properties = new Properties();
        properties.putAll(params);
        VariableTokenHandler handler = new VariableTokenHandler(properties);
        GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
        return parser.parse(text);
    }

    public Set<String> parseParameter(String text) {
        text = String.format("<root>%s</root>", text);
        SqlNode sqlNode = parseXml2SqlNode(text);
        HashSet<String> set = new HashSet<>();
        sqlNode.applyParameter(set);
        return set;
    }

    private SqlNode parseXml2SqlNode(String text) {
        SqlNode node = cache.getNodeCache().get(text);
        if (node == null) {
            node = XmlParser.parseXml2SqlNode(text);
            cache.getNodeCache().put(text, node);
        }
        return node;
    }

    /**
     * 解析标签，去除标签，替换 ${}为常量值, #{}保留不变
     *
     * @param sqlNode
     * @param context
     */
    private void parseSqlText(SqlNode sqlNode, DynamicContext context) {
        sqlNode.apply(context);
    }

    /**
     * #{}替换成?，并且将?对应的参数值按顺序保存起来
     *
     * @param context
     */
    private void parseParameter(DynamicContext context) {
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                Object value = context.getOgnlValue(content);
                if (value == null) {
                    throw new RuntimeException("could not found value : " + content);
                }
                context.addParameter(value);
                return "?";
            }
        });
        String sql = tokenParser.parse(context.getSql());
        context.setSql(sql);
    }

    public static void main(String[] args) {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<root>select <if test='minId != null'>id > ${minId} #{minId} <if test='maxId != null'> and id &lt; ${maxId} #{maxId}</if> </if></root>");
        Map<String, Object> map = new HashMap<>();
        map.put("minId", 100);
        map.put("maxId", 500);
        engine.parse(sql, map);
    }
}
