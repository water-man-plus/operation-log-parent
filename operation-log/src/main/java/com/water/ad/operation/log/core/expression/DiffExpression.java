package com.water.ad.operation.log.core.expression;

import com.water.ad.operation.log.core.function.FunctionService;
import com.water.ad.operation.log.core.diff.ObjectDiffHandler;
import com.water.ad.operation.log.util.TemplateUtil;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.common.CompositeStringExpression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象diff表达式
 * <p>
 * diff( {methodA(#p1)},{methodB(#p2)} ) preObject引用方法，postObject引用方法
 * <p>
 * diff( {methodA(#p1)},{#objB} ) preObject引用方法，postObject引用参数对象
 * <p>
 * diff( {#objA},{#methodB(#p2)} ) preObject引用参数对象，postObject引用方法
 * <p>
 * diff( {#objA},{#objB} ) preObject引用参数对象，postObject引用参数对象
 *
 * @author yyq
 * @create 2022-03-09
 **/
public class DiffExpression extends BaseLogRecordExpression {

    private Expression preExpression;
    private Expression postExpression;

    private ObjectDiffHandler objectDiffHandler;
    private FunctionService functionService;


    public DiffExpression(String expressionString, ObjectDiffHandler objectDiffHandler, FunctionService functionService) {
        super(expressionString);
        Assert.notNull(objectDiffHandler, "objectDiffHandler is null");
        Assert.notNull(functionService, "functionService is null");
        this.expressionString = expressionString;
        this.objectDiffHandler = objectDiffHandler;
        this.functionService = functionService;

        parse();
    }

    @Override
    Expression[] beforeExecute() {
        List<Expression> expressions = new ArrayList<>();
        if (preExpression instanceof CustomMethodExpression) {
            expressions.add(preExpression);
        }
        if (postExpression instanceof CustomMethodExpression) {
            expressions.add(postExpression);
        }
        if (expressions.isEmpty()) {
            return null;
        }
        return expressions.toArray(new Expression[0]);
    }

    private void parse() {
        Expression expression = spelExpressionParser.parseExpression(expressionString,
                new TemplateParserContext("{", "}"));
        if (!(expression instanceof CompositeStringExpression)) {
            throw new ExpressionException(String.format("expressionString [diff(%s)] incongruity for diff expression", expressionString));
        }
        CompositeStringExpression compositeStringExpression = (CompositeStringExpression) expression;
        Expression[] expressions = compositeStringExpression.getExpressions();
        if (expressions.length != 3) {
            throw new ExpressionException(String.format("expressionString [diff(%s)] incongruity for diff expression", expressionString));
        }
        //中间的逗号，跳过
        preExpression = transferExpression(expressions[0]);
        postExpression = transferExpression(expressions[2]);

    }

    private Expression transferExpression(Expression expression) {
        if (TemplateUtil.isSourceSpelExpression(expression.getExpressionString())) {
            //引用对象
            return expression;
        } else {
            //引用方法
            return new CustomMethodExpression(expression.getExpressionString(), functionService);
        }
    }


    @Override
    public String getValue(EvaluationContext context) throws EvaluationException {
        Object pre = preExpression.getValue(context);
        Object post = postExpression.getValue(context);
        return objectDiffHandler.diff(pre, post, null);
    }
}
