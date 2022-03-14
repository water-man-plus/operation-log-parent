package com.water.ad.operation.log.core.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.EvaluationException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 基于spring容器中bean的方法调用
 *
 * @author yyq
 * @create 2022-02-18
 **/
@Slf4j
public class SpringContextBeanFunctionServiceImpl extends MethodResolver implements FunctionService {

    private BeanFactory beanFactory;

    private static final String FULL_NAME_SPIL = ".";

    public SpringContextBeanFunctionServiceImpl(BeanFactory beanFactory) {
        if (beanFactory == null) {
            throw new IllegalArgumentException("beanFactory can not be null");
        }
        this.beanFactory = beanFactory;
    }


    /**
     * 这里的自定义名称代表方法全名称，如fm.lizhi.ad.operation.log.handler.function.SpringContextFunctionServiceImpl.apply
     *
     * @param functionName 自定义函数名称
     * @param args         参数
     * @return
     */
    @Override
    public Object apply(Object targetObject, String functionName, Object[] args) {
        if (!functionName.contains(FULL_NAME_SPIL)) {
            return null;
        }
        int indexOf = functionName.lastIndexOf(FULL_NAME_SPIL);
        String classFullName = functionName.substring(0, indexOf);
        String methodName = functionName.substring(indexOf + 1);
        Object object;
        Class<?> targetClass;
        try {
            targetClass = Class.forName(classFullName);
        } catch (ClassNotFoundException e) {
            String errorMsg = String.format("not found class [%s]", classFullName);
            log.error(errorMsg, e);
            throw new EvaluationException(errorMsg, e);
        }
        object = beanFactory.getBean(targetClass);
        Method method = resolve(object, methodName, args);
        return ReflectionUtils.invokeMethod(method, object, args);
    }
}
