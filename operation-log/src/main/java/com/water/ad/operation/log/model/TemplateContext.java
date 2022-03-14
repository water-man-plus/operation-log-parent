package com.water.ad.operation.log.model;

import com.water.ad.operation.log.core.expression.LogRecordExpression;
import lombok.*;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;


/**
 * 模板上下文
 *
 * @author yyq
 * @create 2022-02-21
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateContext {

    private LogRecordExpression detailExpression;
    /**
     * 业务标识号，使用spel表达式
     */
    private Expression biNoExpression;
    /**
     * 操作人，使用spel表达式
     */
    private Expression operatorExpression;

    /**
     * 记录条件，使用spel表达式
     */
    private Expression conditionExpression;

    /**
     * 操作日志类别，常量字符串
     */
    private String category;

    private EvaluationContext evaluationContext;

    /**
     * 切面拦截的目标对象
     */
    private Object targetObject;

}
