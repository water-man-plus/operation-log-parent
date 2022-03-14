package com.water.ad.operation.log.core.function;

/**
 * @author yyq
 * @create 2022-02-17
 **/
public class DefaultFunctionServiceImpl implements FunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    public DefaultFunctionServiceImpl(ParseFunctionFactory parseFunctionFactory) {
        this.parseFunctionFactory = parseFunctionFactory;
    }

    /**
     * 自定义函数执行
     *
     * @param functionName 自定义函数名称
     * @param args         method args
     * @return method execute result
     */
    @Override
    public Object apply(Object targetObject, String functionName, Object[] args) {

        ParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            //这里原值返回还是null返回好呢
            return null;
        }
        return function.apply(args);
    }
}
