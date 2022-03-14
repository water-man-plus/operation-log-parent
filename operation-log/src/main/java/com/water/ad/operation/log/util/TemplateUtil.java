package com.water.ad.operation.log.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yyq
 * @create 2022-03-12
 **/
public class TemplateUtil {


    /**
     * 判断是否是原始的spel表达式
     *
     * @param expression
     * @return
     */
    public static boolean isSourceSpelExpression(String expression) {
        return StringUtils.isNotEmpty(expression) &&
                expression.startsWith("#") ||
                expression.startsWith("@");
    }
}
