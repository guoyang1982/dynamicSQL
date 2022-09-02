package com.gy.dsql.node;

import com.gy.dsql.context.DynamicContext;
//import com.gy.dsql.token.TokenHandler;
//import com.gy.dsql.token.TokenParser;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;

import java.util.Set;


public class TextSqlNode implements SqlNode {
    String text;

    public TextSqlNode(String text) {
        this.text = text;
    }
    @Override
    public boolean apply(DynamicContext context) {
        //解析常量值 ${xxx}
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
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
