package com.gy.dsql.token;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * @author guoyang
 * @date 2022/9/5 5:50 下午
 */
public class VariableTokenHandler implements TokenHandler{
    private final Properties variables;
    private final boolean enableDefaultValue;
    private final String defaultValueSeparator;
    private final String valueBracketSign;

    public VariableTokenHandler(Properties variables) {
        this.variables = variables;
        this.enableDefaultValue = Boolean.parseBoolean(this.getPropertyValue("org.apache.ibatis.parsing.PropertyParser.enable-default-value", "true"));
        this.defaultValueSeparator = this.getPropertyValue("org.apache.ibatis.parsing.PropertyParser.default-value-separator", ":");
        this.valueBracketSign = "@";
    }

    private String getPropertyValue(String key, String defaultValue) {
        return this.variables == null ? defaultValue : this.variables.getProperty(key, defaultValue);
    }

    @Override
    public String handleToken(String content) {
        if (this.variables == null) {
            return "${" + content + "}";
        }

        //需要处理动态条件 来判断是否增加此条件和填充最终的值
        if (content.contains(valueBracketSign)) {
            //and dt = '@dt@' TO  and dt='1'
            int firstIndex = StringUtils.indexOf(content, valueBracketSign);
            int lastIndex = StringUtils.lastIndexOf(content, valueBracketSign);

            if (firstIndex < lastIndex) {
                String prefix = content.substring(0, firstIndex);
                String valueKey = content.substring(firstIndex + 1, lastIndex);
                String suffix = content.substring(lastIndex + 1);

                String value = this.variables.getProperty(valueKey);
                if (StringUtils.isBlank(value)) {
                    return "";
                } else {
                    return prefix + value + suffix;
                }
            }
        }

        //需要处理动态条件，来填充最终的值
        String key = content;
        if (this.enableDefaultValue) {
            int separatorIndex = content.indexOf(this.defaultValueSeparator);
            String defaultValue = null;
            if (separatorIndex >= 0) {
                key = content.substring(0, separatorIndex);
                defaultValue = content.substring(separatorIndex + this.defaultValueSeparator.length());
            }

            if (defaultValue != null) {
                return this.variables.getProperty(key, defaultValue);
            }
        }

        if (this.variables.containsKey(key)) {
            return this.variables.getProperty(key);
        }

        return "${" + content + "}";
    }
}
