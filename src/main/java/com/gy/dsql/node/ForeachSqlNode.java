package com.gy.dsql.node;

import com.gy.dsql.context.DynamicContext;
import com.gy.dsql.token.TokenHandler;
import com.gy.dsql.token.TokenParser;
import com.gy.dsql.util.OgnlUtil;
import com.gy.dsql.util.RegexUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ForeachSqlNode implements SqlNode {

    String collection;
    String open;
    String close;
    String separator;
    String item;
    String index;
    SqlNode contents;

    String indexDataName;

    public ForeachSqlNode(String collection, String open, String close, String separator, String item, String index, SqlNode contents) {
        this.collection = collection;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.item = item;
        this.index = index;
        this.contents = contents;

        this.indexDataName = String.format("__index_%s", collection);
    }

    @Override
    public boolean apply(DynamicContext context) {
        context.appendSql(" ");//标签类SqlNode先拼接空格，和前面的内容隔开
        Iterable<?> iterable = OgnlUtil.getIterable(collection, context.getData());
        if (!iterable.iterator().hasNext()) {
            return true;
        }
        int currentIndex = 0;
        ArrayList<Integer> indexs = new ArrayList<>();
        context.getData().put(indexDataName, indexs);
        context.appendSql(open);
        for (Object o : iterable) {
            ((ArrayList<Integer>) context.getData().get(indexDataName)).add(currentIndex);
            //不是第一次，需要拼接分隔符
            if (currentIndex != 0) {
                context.appendSql(separator);
            }
            DynamicContext proxy = new DynamicContext(context.getData());
            proxy.getData().put(item,o);
            String childSqlText = getChildText(proxy, currentIndex);
            context.appendSql(childSqlText);
            currentIndex++;
        }
        context.appendSql(close);
        return true;
    }

    @Override
    public void applyParameter(Set<String> set) {
        set.add(collection);
        Set<String> temp = new HashSet<>();
        contents.applyParameter(set);
        for (String key : temp) {
            if (key.matches(item + "[.,:\\s\\[]")) {
                continue;
            }
            if (key.matches(index + "[.,:\\s\\[]")) {
                continue;
            }
            set.add(key);
        }
    }

    public String getChildText(DynamicContext proxy, int currentIndex) {
        String newItem = String.format("%s[%d]", collection, currentIndex);  //ognl可以直接获取  aaa[0]  形式的值
        String newIndex = String.format("%s[%d]", indexDataName, currentIndex);

        this.contents.apply(proxy);
        String parse = replaceVar(proxy, newItem, newIndex);
        return parse;
    }


//    private static class FilteredDynamicContext extends Context {
//        private final Context delegate;
//        private final int index;
//        private final String itemIndex;
//        private final String item;
//
//        public FilteredDynamicContext(Configuration configuration,Context delegate, String itemIndex, String item, int i) {
//            super(configuration, null);
//            this.delegate = delegate;
//            this.index = i;
//            this.itemIndex = itemIndex;
//            this.item = item;
//        }
//
//
//        @Override
//        public String getSql() {
//            return delegate.getSql();
//        }
//
//        @Override
//        public void appendSql(String sql) {
//            GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
//                String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
//                if (itemIndex != null && newContent.equals(content)) {
//                    newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
//                }
//                return "#{" + newContent + "}";
//            });
//
//            delegate.appendSql(parser.parse(sql));
//        }
//
//    }

    private String replaceVar(DynamicContext proxy, String newItem, String newIndex) {
        String sql = proxy.getSql();
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                //item替换成自己的变量名: item[0]  item[1] item[2] ......
                String replace = RegexUtil.replace(content, item, newItem);
                if (replace.equals(content))
                //index替换成自己的变量名: __index_xxx[0]  __index_xxx[1] __index_xxx[2] ......
                {
                    replace = RegexUtil.replace(content, index, newIndex);
                }
                StringBuilder builder = new StringBuilder();
                return builder.append("#{").append(replace).append("}").toString();
            }
        });
        return tokenParser.parse(sql);
    }


}
