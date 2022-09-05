package com.gy.dsql.node;

import com.gy.dsql.context.DynamicContext;
//import com.gy.dsql.token.TokenHandler;
//import com.gy.dsql.token.TokenParser;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;

import java.util.Set;


public class TextSqlNode implements SqlNode {
    String text;
    boolean ENABLE_DEFAULT_VALUE = true;
    String DEFAULT_VALUE_SEPARATOR = ":";
    public TextSqlNode(String text) {
        this.text = text;
    }
    @Override
    public boolean apply(DynamicContext context) {
        //解析常量值 ${xxx}
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {

                //支持默认值功能
                if(ENABLE_DEFAULT_VALUE){
                    String key = paramName;
                    int separatorIndex = paramName.indexOf(DEFAULT_VALUE_SEPARATOR);
                    String defaultValue = null;
                    if (separatorIndex >= 0) {
                        key = paramName.substring(0, separatorIndex);
                        defaultValue = paramName.substring(separatorIndex + DEFAULT_VALUE_SEPARATOR.length());
                        //获取值
                        Object value = context.getOgnlValue(key);
                        return value == null ? defaultValue : value.toString();
                    }else {
                        Object value = context.getOgnlValue(key);
                        return value == null ? "" : value.toString();
                    }
                }
                Object value = context.getOgnlValue(paramName);
                return value == null ? "" : value.toString();
            }
        });
        String s = tokenParser.parse(text);
        context.appendSql(s);
        return true;
    }

    @Override
    public void applyParameter(Set<String> set) {
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        String s = tokenParser.parse(text);

        GenericTokenParser tokenParser2 = new GenericTokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        tokenParser2.parse(s);
    }
    private GenericTokenParser createParser(TokenHandler handler) {
        return new GenericTokenParser("${", "}", handler);
    }
    public boolean isDynamic() {
        DynamicCheckerTokenParser checker = new DynamicCheckerTokenParser();
        GenericTokenParser parser = createParser(checker);
        parser.parse(text);
        return checker.isDynamic();
    }

    private static class DynamicCheckerTokenParser implements TokenHandler {

        private boolean isDynamic;

        public DynamicCheckerTokenParser() {
            // Prevent Synthetic Access
        }

        public boolean isDynamic() {
            return isDynamic;
        }

        @Override
        public String handleToken(String content) {
            this.isDynamic = true;
            return null;
        }
    }
}
