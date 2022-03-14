package com.water.ad.operation.log.core.expression;

import com.water.ad.operation.log.core.function.FunctionService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.expression.*;
import org.springframework.expression.common.CompositeStringExpression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.util.Assert;

/**
 * 自定义方法表达式
 * <p>
 * NOTE：不支持方法没有参数的情况
 * <p>
 * { method(#p1) }    单参数
 * <p>
 * { method(#p1,#p2) } 多参数
 *
 * @author yyq
 * @create 2022-03-09
 **/
public class CustomMethodExpression extends BaseLogRecordExpression {

    private static final String BEFORE_EXECUTE_PREFIX = "$$before.";
    private static final MethodArgsExpressionParser METHOD_ARGS_EXPRESSION_PARSER = new MethodArgsExpressionParser();

    /**
     * 目标方法之前执行
     */
    private boolean beforeExecute;

    private String fullNameMethod;

    /**
     * 涉及到前置执行，这里需要保存执行结果
     */
    private Object value;

    /**
     * 方法参数（spel表达式，这里无效再拆分）
     */
    private Expression[] argsExpressions;

    private FunctionService functionService;

    public CustomMethodExpression(String expressionString, FunctionService functionService) {
        super(expressionString);
        Assert.notNull(functionService, "function service is null");
        this.functionService = functionService;
        //解析当前表达式
        parse();
    }

    @Override
    Expression[] beforeExecute() {
        if (beforeExecute) {
            return new Expression[]{this};
        }
        return null;
    }

    @Override
    public Object getValue(EvaluationContext context) throws EvaluationException {
        if (value != null) {
            return value;
        }
        Object[] args = getArgsValue(context);
        value = functionService.apply(context.getRootObject().getValue(), fullNameMethod, args);
        return value;
    }

    private void parse() {
        //调用目标方法前执行
        if (expressionString.startsWith(BEFORE_EXECUTE_PREFIX)) {
            beforeExecute = true;
        }
        //这里使用自定义的解析器，默认的遇到method(#p1,#p2多参数会报错
        Expression expression = METHOD_ARGS_EXPRESSION_PARSER.parseExpression(expressionString, new TemplateParserContext("(", ")"));
        if (!(expression instanceof CompositeStringExpression)) {
            throw new ExpressionException(String.format("expressionString [{%s}] incongruity for CustomMethod expression", expression));
        }
        CompositeStringExpression compositeStringExpression = (CompositeStringExpression) expression;
        Expression[] expressions = compositeStringExpression.getExpressions();
        if (expressions.length != 2) {
            throw new ExpressionException(String.format("expressionString [{%s}] incongruity for CustomMethod expression", expression));
        }
        //方法全名
        fullNameMethod = expressions[0].getExpressionString().replace(BEFORE_EXECUTE_PREFIX, "");
        //参数
        String argsExpression = expressions[1].getExpressionString();
        String[] argsExpressionString = argsExpression.split(",");
        argsExpressions = new Expression[argsExpressionString.length];
        for (int i = 0; i < argsExpressionString.length; i++) {
            argsExpressions[i] = spelExpressionParser.parseExpression(argsExpressionString[i]);
        }
    }


    /**
     * 方法参数解析
     *
     * @param context
     * @return
     */
    private Object[] getArgsValue(EvaluationContext context) {
        if (ArrayUtils.isEmpty(argsExpressions)) {
            return null;
        }
        Object[] args = new Object[argsExpressions.length];
        for (int i = 0; i < argsExpressions.length; i++) {
            Expression expression = argsExpressions[i];
            Object arg = argsExpressions[i].getValue(context);
            if (arg == null) {
                throw new EvaluationException(expression.getExpressionString(), String.format("Cannot call get value for [%s] expression",
                        expression.getExpressionString()));
            }
            args[i] = arg;
        }
        return args;
    }

    public boolean isBeforeExecute() {
        return beforeExecute;
    }


    /**
     * 方法参数解析器
     */
    static class MethodArgsExpressionParser extends TemplateAwareExpressionParser {
        @Override
        protected Expression doParseExpression(String expressionString, ParserContext context) throws ParseException {
            return new LiteralExpression(expressionString);
        }
    }
}
