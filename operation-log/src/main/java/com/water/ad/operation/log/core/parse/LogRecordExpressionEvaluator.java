package com.water.ad.operation.log.core.parse;

import com.water.ad.operation.log.core.expression.LogRecordExpression;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表达式执行器
 *
 * @author yyq
 * @create 2022-02-17
 **/
public class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {

    private Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    /**
     * Create a new instance with the specified {@link ExpressionParser}.
     *
     * @param parser
     */
    public LogRecordExpressionEvaluator(ExpressionParser parser) {
        super(parser);
    }

    /**
     * expression 解析
     */
    public LogRecordExpression parseExpression(String conditionExpression, AnnotatedElementKey methodKey) {
        return (LogRecordExpression) getExpression(this.expressionCache, methodKey, conditionExpression);
    }

    public EvaluationContext createEvaluationContext(Method method, Object[] args, Object targetObject) {
        return new LogRecordEvaluationContext(targetObject, method, args, this.getParameterNameDiscoverer());
    }

}
