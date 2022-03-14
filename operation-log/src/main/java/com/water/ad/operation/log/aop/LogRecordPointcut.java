package com.water.ad.operation.log.aop;

import com.water.ad.operation.log.annotation.LogRecordAnnotation;
import com.water.ad.operation.log.core.LogRecordContext;
import com.water.ad.operation.log.core.OperatorGetService;
import com.water.ad.operation.log.core.expression.LogRecordExpression;
import com.water.ad.operation.log.core.parse.LogRecordExpressionEvaluator;
import com.water.ad.operation.log.core.parse.LogRecordExpressionParser;
import com.water.ad.operation.log.core.record.LogRecordService;
import com.water.ad.operation.log.model.LogRecord;
import com.water.ad.operation.log.model.TemplateContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Date;


/**
 * @author yyq
 */
@Aspect
@Slf4j
@Order(1)
public class LogRecordPointcut {


    private LogRecordExpressionEvaluator logRecordExpressionEvaluator;
    private LogRecordService iLogRecordService;
    private OperatorGetService operatorGetService;

    public LogRecordPointcut(LogRecordExpressionEvaluator logRecordExpressionEvaluator,
                             LogRecordService logRecordService,
                             OperatorGetService operatorGetService,
                             LogRecordExpressionParser logRecordExpressionParser) {
        Assert.notNull(logRecordExpressionEvaluator, "logRecordExpressionEvaluator can not be null");
        Assert.notNull(logRecordService, "logRecordService can not be null");
        Assert.notNull(operatorGetService, "operatorGetService can not be null");
        this.logRecordExpressionEvaluator = logRecordExpressionEvaluator;
        this.iLogRecordService = logRecordService;
        this.operatorGetService = operatorGetService;
    }

    @Around("@annotation(com.water.ad.operation.log.annotation.LogRecordAnnotation)")
    public Object record(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        Object ret = null;
        MethodExecuteResult methodExecuteResult = new MethodExecuteResult();
        LogRecordAnnotation logRecordAnnotation = null;
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class targetClass = target.getClass();
        TemplateContext templateContext = null;
        try {
            logRecordAnnotation = method.getAnnotation(LogRecordAnnotation.class);
            templateContext = parseTemplate(method, args, target, logRecordAnnotation);
            LogRecordContext.putVariable(LogRecordContext.TEMPLATE_CONTEXT_KEY, templateContext);
            processBeforeExecuteFunction(templateContext);
        } catch (Exception e) {
            methodExecuteResult.setSuccess(false);
            log.error("log record parse before function exception {}", e.getMessage(), e);
        }
        try {
            ret = joinPoint.proceed(args);
        } catch (Exception e) {
            methodExecuteResult = new MethodExecuteResult(false, e, e.getMessage());
        }
        try {
            if (logRecordAnnotation != null && templateContext != null && methodExecuteResult.isSuccess()) {
                recordExecute(ret, templateContext, logRecordAnnotation);
            }
        } catch (Exception t) {
            //记录日志错误不要影响业务
            log.error("log record parse exception", t);
        } finally {
            LogRecordContext.clear();
        }
        if (methodExecuteResult.getThrowable() != null) {
            throw methodExecuteResult.getThrowable();
        }
        return ret;
    }


    /**
     * 模板解析
     *
     * @param logRecordAnnotation logRecordAnnotation
     * @return TemplateContext
     */
    private TemplateContext parseTemplate(Method method, Object[] args, Object targetObject,
                                          LogRecordAnnotation logRecordAnnotation) {
        String detail = logRecordAnnotation.detail();
        String bizNo = logRecordAnnotation.bizNo();
        String operator = logRecordAnnotation.operator();
        String category = logRecordAnnotation.category();
        String condition = logRecordAnnotation.condition();
        TemplateContext template = new TemplateContext();

        if (StringUtils.isEmpty(bizNo)) {
            throw new ExpressionException("bizNo expression is empty");
        }
        if (StringUtils.isEmpty(category)) {
            throw new ExpressionException("category expression is empty");
        }
        if (StringUtils.isEmpty(condition)) {
            throw new ExpressionException("condition expression is empty");
        }

        //自定义模板解析
        EvaluationContext context = logRecordExpressionEvaluator.createEvaluationContext(method, args, targetObject);
        AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetObject.getClass());

        LogRecordExpression detailExpression = logRecordExpressionEvaluator.parseExpression(detail, annotatedElementKey);
        Expression bizNoExpression = logRecordExpressionEvaluator.parseExpression(bizNo, annotatedElementKey);
        if (StringUtils.isNotEmpty(operator)) {
            Expression operatorExpression = logRecordExpressionEvaluator.parseExpression(operator, annotatedElementKey);
            template.setOperatorExpression(operatorExpression);

        }
        Expression conditionExpression = logRecordExpressionEvaluator.parseExpression(condition, annotatedElementKey);
        template.setDetailExpression(detailExpression);
        template.setBiNoExpression(bizNoExpression);
        template.setConditionExpression(conditionExpression);
        template.setEvaluationContext(context);
        template.setTargetObject(targetObject);
        return template;
    }

    /**
     * 自定义函数-前置执行逻辑
     *
     * @param template template
     */
    private void processBeforeExecuteFunction(TemplateContext template) {

        Expression[] beforeExpressions = template.getDetailExpression().beforeExecute();
        if (beforeExpressions != null) {
            for (Expression beforeExpression : beforeExpressions) {
                beforeExpression.getValue(template.getEvaluationContext());
            }
        }
    }

    /**
     * @param ret                 target method return
     * @param template            template
     * @param logRecordAnnotation logRecordAnnotation
     */
    private void recordExecute(Object ret, TemplateContext template, LogRecordAnnotation
            logRecordAnnotation) {

        EvaluationContext context = template.getEvaluationContext();
        //返回值也放到context中
        context.setVariable("ret", ret);
        //是否记录
        boolean condition = handlerCondition(template);
        if (!condition) {
            log.warn("condition expression [{}] is false ,skip record log", template.getConditionExpression().getExpressionString());
            return;
        }
        //指定所有操作内容
        String content = logRecordAnnotation.detail();
        if (LogRecordContext.getVariables().containsKey(LogRecordContext.CUSTOM_LOG_DETAIL_KEY)) {
            content = (String) LogRecordContext.getVariables().get(LogRecordContext.CUSTOM_LOG_DETAIL_KEY);
        } else {
            content = template.getDetailExpression().getValue(context, String.class);
        }
        //指定附加内容
        String appendContent;
        if (LogRecordContext.getVariables().containsKey(LogRecordContext.CUSTOM_LOG_APPEND_DETAIL_KEY)) {
            appendContent = (String) LogRecordContext.getVariables().get(LogRecordContext.CUSTOM_LOG_APPEND_DETAIL_KEY);
            content = content + appendContent;
        }
        String category = logRecordAnnotation.category();
        String bizNo = handlerBizNo(template);
        String operator = handlerOperator(template);
        iLogRecordService.record(LogRecord.builder()
                .time(new Date())
                .operatorId(operator)
                .category(category)
                .bizNo(bizNo)
                .detail(content).build());
    }

    private String handlerBizNo(TemplateContext template) {
        String bizNo = template.getBiNoExpression().getValue(template.getEvaluationContext(), String.class);
        if (StringUtils.isEmpty(bizNo)) {
            throw new EvaluationException("bizNo is null");
        }
        return bizNo;
    }

    private String handlerOperator(TemplateContext template) {
        String operator;
        if (template.getOperatorExpression() != null) {
            operator = template.getOperatorExpression().getValue(template.getEvaluationContext(), String.class);
        } else {
            if (operatorGetService.getUser() == null) {
                throw new EvaluationException("operatorGetService return null info");
            }
            operator = operatorGetService.getUser().getId();
        }
        if (StringUtils.isEmpty(operator)) {
            throw new EvaluationException("operator is null");
        }
        return operator;
    }

    private boolean handlerCondition(TemplateContext template) {
        return template.getConditionExpression().getValue(template.getEvaluationContext(), Boolean.class);
    }
}
