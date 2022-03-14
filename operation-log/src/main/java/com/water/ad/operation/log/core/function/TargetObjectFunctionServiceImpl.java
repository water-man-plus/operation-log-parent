package com.water.ad.operation.log.core.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 基于切面代理的目标对象所属方法调用
 *
 * @author yyq
 * @create 2022-02-18
 **/
@Slf4j
public class TargetObjectFunctionServiceImpl extends MethodResolver implements FunctionService {

    private static final String FULL_NAME_SPIL = ".";

    /**
     * @param targetObject 目标方法所属对象
     * @param functionName 自定义函数名称
     * @param args         参数
     * @return
     */
    @Override
    public Object apply(Object targetObject, String functionName, Object[] args) {
        if (functionName.contains(FULL_NAME_SPIL)) {
            return null;
        }
        Method method = resolve(targetObject, functionName, args);
        return ReflectionUtils.invokeMethod(method, targetObject, args);

    }
}
