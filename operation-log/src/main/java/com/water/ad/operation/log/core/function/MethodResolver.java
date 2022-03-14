package com.water.ad.operation.log.core.function;

import com.alibaba.fastjson.JSON;
import org.springframework.expression.EvaluationException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author yyq
 * @create 2022-03-12
 **/
public class MethodResolver {
    public Method resolve(Object targetObject, String functionName, Object[] args) {
        Class<?> targetClass = targetObject.getClass();
        Method method;
        Class[] paramsType;
        paramsType = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramsType[i] = args[i].getClass();
        }
        method = ReflectionUtils.findMethod(targetClass, functionName, paramsType);
        if (method == null) {
            throw new EvaluationException(String.format("not found method [%s] param %s for class [%s]", functionName, JSON.toJSONString(paramsType), targetObject.getClass().getName()));
        }
        return method;
    }

}
