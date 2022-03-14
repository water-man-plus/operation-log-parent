package com.water.ad.operation.log.core.expression;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.common.CompositeStringExpression;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日志表达式
 *
 * @author yyq
 * @create 2022-03-09
 **/
public class LogRecordExpression extends BaseLogRecordExpression {


    private Expression expression;

    public LogRecordExpression(String expressionString, Expression expression) {
        super(expressionString);
        Assert.notNull(expression, "expression is null");
        this.expression = expression;
    }

    @Override
    public Object getValue(EvaluationContext context) throws EvaluationException {
        return expression.getValue(context);
    }

    @Override
    public Expression[] beforeExecute() {
        List<Expression> beforeExpression = new ArrayList<>();
        if (expression instanceof CompositeStringExpression) {
            Expression[] expressions = ((CompositeStringExpression) expression).getExpressions();
            for (Expression exp : expressions) {
                if (exp instanceof BaseLogRecordExpression) {
                    Expression[] bex = ((BaseLogRecordExpression) exp).beforeExecute();
                    if (bex != null) {
                        beforeExpression.addAll(Arrays.asList(bex));
                    }
                }
            }
        }
        if (expression instanceof BaseLogRecordExpression) {
            Expression[] bex = ((BaseLogRecordExpression) expression).beforeExecute();
            if (bex != null) {
                beforeExpression.addAll(Arrays.asList(bex));
            }
        }
        return beforeExpression.toArray(new Expression[]{});
    }
}
