package com.water.ad.operation.log.core.function;

import java.util.List;

/**
 * 按照加入 {@link PrioritizedFunctionServiceImpl#functionServices} 集合中的顺序作为优先级执行
 *
 * @author yyq
 * @create 2022-02-21
 **/
public class PrioritizedFunctionServiceImpl implements FunctionService {

    private List<FunctionService> functionServices;

    public PrioritizedFunctionServiceImpl(List<FunctionService> functionServices) {
        if (functionServices == null) {
            throw new IllegalArgumentException("functionServices can not be null");
        }
        this.functionServices = functionServices;
    }

    @Override
    public Object apply(Object targetObject, String functionName, Object[] args) {
        for (FunctionService functionService : functionServices) {
            Object value = functionService.apply(targetObject, functionName, args);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
