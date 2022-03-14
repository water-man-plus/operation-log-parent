package com.water.ad.operation.log.core.function;

/**
 * 自定义方法执行接口
 *
 * @author yyq
 * @create 2022-02-17
 **/
public interface FunctionService {


    /**
     * @param targetObject 目标方法所属对象
     * @param functionName 自定义函数名称
     * @param args         参数
     * @return function execute result
     */
    Object apply(Object targetObject, String functionName, Object[] args);
}
