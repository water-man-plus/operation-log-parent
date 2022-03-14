package com.water.ad.operation.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *
 * @author yyq
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogRecordAnnotation {

    String BEFORE_METHOD_PREFIX = "beforeExecute.";

    /**
     * 操作日志的执行人
     *
     * @return operator
     */
    String operator() default "";

    /**
     * 业务标识号，支持spel表达式
     *
     * @return bizNo
     */
    String bizNo();

    /**
     * 操作日志种类，字符串常量
     *
     * @return category
     */
    String category();

    /**
     * 操作内容
     * 普通字符串模式：修改了名称，从{xxx}修改为{xxx}
     * diff模式：更新的应用，diff({xxx},{xxx})
     *
     * @return detail
     */
    String detail() default "";

    /**
     * diff操作时指定只对应特定的field,默认全部,逗号分割
     * field1,field2
     *
     * @return diffField
     */
    String diffField() default "";

    /**
     * 是否记录操作日志判断表达式，支持spel表达式，返回值必须是布尔类型
     *
     * @return condition boolean
     */
    String condition() default "true";

    /**
     * 普通模板（非diff情况）下的返回值映射
     * JSON string mapper<p>
     * A typical value should look like: <p>
     * ---- {"0": "disabled", "1": "enabled"} ----<p>
     * Map the field values like [0/1] to a human-readable string like [enabled/disabled] <p>
     *
     * @return
     */
    String commonValueMapping() default "{}";

}
