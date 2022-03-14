package com.water.ad.operation.log.core.expression;


import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * @author yyq
 * @create 2022-03-09
 **/
public abstract class BaseLogRecordExpression implements Expression {

    static SpelExpressionParser spelExpressionParser = new SpelExpressionParser();


    String expressionString;

    public BaseLogRecordExpression(String expressionString) {
        Assert.notNull(expressionString, "expressionString is null");
        this.expressionString = expressionString;
    }

    /**
     * 目标方法前置执行
     *
     * @return expression array execute before target method
     */
    abstract Expression[] beforeExecute();


    @Override
    public String getExpressionString() {
        return expressionString;
    }

    /**
     * get expression value
     *
     * @param context context
     * @return value
     * @throws EvaluationException
     */
    @Override
    public abstract Object getValue(EvaluationContext context) throws EvaluationException;

    @Override
    public <T> T getValue(EvaluationContext context, Class<T> expectedResultType)
            throws EvaluationException {
        Object value = getValue(context);
        return ExpressionUtils.convertTypedValue(context, new TypedValue(value), expectedResultType);
    }

    @Override
    public Object getValue() throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValue() on a BaseEmptyExpression ");
    }

    @Override
    public Class<?> getValueType(EvaluationContext context) {
        Assert.notNull(context, "EvaluationContext is required");
        TypeDescriptor typeDescriptor = new TypedValue(getValue(context)).getTypeDescriptor();
        return (typeDescriptor != null ? typeDescriptor.getType() : null);
    }


    @Override
    public <T> T getValue(Class<T> expectedResultType) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValue(Class<T> expectedResultType) on a BaseEmptyExpression");
    }

    @Override
    public Object getValue(Object rootObject) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValue(Object rootObject) on a BaseEmptyExpression");

    }

    @Override
    public <T> T getValue(Object rootObject, Class<T> desiredResultType) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValue(Object rootObject, Class<T> desiredResultType) on a BaseEmptyExpression");

    }

    @Override
    public Object getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call  getValue(EvaluationContext context, Object rootObject) on a BaseEmptyExpression");
    }

    @Override
    public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType)
            throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call  getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType) on a BaseEmptyExpression");
    }

    @Override
    public Class<?> getValueType() {
        throw new EvaluationException(this.expressionString, "Cannot call getValueType() on a BaseEmptyExpression");
    }

    @Override
    public Class<?> getValueType(Object rootObject) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValueType(Object rootObject) on a BaseEmptyExpression");
    }

    @Override
    public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValueType(EvaluationContext context, Object rootObject) on a BaseEmptyExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor() {
        throw new EvaluationException(this.expressionString, "Cannot call getValueTypeDescriptor() on a BaseEmptyExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValueTypeDescriptor(Object rootObject) on a BaseEmptyExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
        throw new EvaluationException(this.expressionString, "Cannot call getValueTypeDescriptor(EvaluationContext context) on a BaseEmptyExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject)
            throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call getValueTypeDescriptor(EvaluationContext context, Object rootObject) on a BaseEmptyExpression");
    }

    @Override
    public boolean isWritable(Object rootObject) throws EvaluationException {
        return false;
    }

    @Override
    public boolean isWritable(EvaluationContext context) {
        return false;
    }

    @Override
    public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
        return false;
    }

    @Override
    public void setValue(Object rootObject, Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a BaseEmptyExpression");
    }

    @Override
    public void setValue(EvaluationContext context, Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a BaseEmptyExpression");
    }

    @Override
    public void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a BaseEmptyExpression");
    }
}
