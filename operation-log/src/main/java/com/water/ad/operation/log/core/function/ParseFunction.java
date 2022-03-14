package com.water.ad.operation.log.core.function;

/**
 * @author yyq
 * @create 2022-02-17
 **/
public interface ParseFunction {

    /**
     * 方法名称
     *
     * @return
     */
    String functionName();

    /**
     * 方法执行
     *
     * @param args
     * @return
     */
    Object apply(Object[] args);
}
