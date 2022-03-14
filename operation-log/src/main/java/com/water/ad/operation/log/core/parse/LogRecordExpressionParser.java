package com.water.ad.operation.log.core.parse;

import com.water.ad.operation.log.core.expression.CustomMethodExpression;
import com.water.ad.operation.log.core.expression.DiffExpression;
import com.water.ad.operation.log.core.expression.LogRecordExpression;
import com.water.ad.operation.log.core.function.FunctionService;
import com.water.ad.operation.log.core.diff.ObjectDiffHandler;
import com.water.ad.operation.log.util.RegexpUtil;
import com.water.ad.operation.log.util.TemplateUtil;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * @author yyq
 * @create 2022-03-09
 **/
public class LogRecordExpressionParser extends TemplateAwareExpressionParser {

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    /**
     * diff模板解析
     */
    private static final ParserContext DIFF_PARSE_CONTEXT = new DiffParserContext();
    /**
     * 普通模板解析
     */
    private static final ParserContext COMMON_PARSER_CONTEXT = new CommonParserContext();
    private static final String DIFF_TEMPLATE_REG = "diff\\(.+\\)";
    private static final String CUSTOM_METHOD_REG = "\\(.+\\)";
    private static final String COMMON_TEMPLATE_REG = "\\{.+\\}";

    private ObjectDiffHandler objectDiffHandler;
    private FunctionService functionService;

    public LogRecordExpressionParser(FunctionService functionService, ObjectDiffHandler objectDiffHandler) {
        Assert.notNull(objectDiffHandler, "objectDiffHandler is null");
        Assert.notNull(objectDiffHandler, "functionService is null");
        this.objectDiffHandler = objectDiffHandler;
        this.functionService = functionService;
    }

    /**
     * @param expressionString 表达式
     * @return TemplateExpression
     * @throws ParseException
     */
    @Override
    public LogRecordExpression parseExpression(String expressionString) throws ParseException {

        Expression expression;
        //diff expression contain diff()
        if (RegexpUtil.hasStr(expressionString, DIFF_TEMPLATE_REG)) {
            expression = super.parseExpression(expressionString, DIFF_PARSE_CONTEXT);
        } else if (RegexpUtil.hasStr(expressionString, COMMON_TEMPLATE_REG)) {
            //common expression,contain {}
            expression = super.parseExpression(expressionString, COMMON_PARSER_CONTEXT);
        } else if (TemplateUtil.isSourceSpelExpression(expressionString)) {
            //spel表达式
            expression = super.parseExpression(expressionString);
        } else {
            //常量
            expression = new LiteralExpression(expressionString);
        }
        return new LogRecordExpression(expressionString, expression);
    }

    @Override
    protected Expression doParseExpression(String expressionString, ParserContext context) throws ParseException {
        Expression expression;
        if (context instanceof DiffParserContext) {
            // diff expression
            expression = new DiffExpression(expressionString, objectDiffHandler, functionService);
        } else if (RegexpUtil.hasStr(expressionString, CUSTOM_METHOD_REG)) {
            //method expression
            expression = new CustomMethodExpression(expressionString, functionService);
        } else {
            //spel expression
            expression = SPEL_EXPRESSION_PARSER.parseExpression(expressionString);
        }
        return expression;
    }


    public static class DiffParserContext implements ParserContext {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "diff(";
        }

        @Override
        public String getExpressionSuffix() {
            return ")";
        }
    }

    public static class CommonParserContext implements ParserContext {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    }

}
