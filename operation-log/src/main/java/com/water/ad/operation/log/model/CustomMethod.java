package com.water.ad.operation.log.model;

import lombok.*;

import java.util.List;

/**
 * 自定义函数
 *
 * @author yyq
 * @create 2022-02-21
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomMethod {

    /**
     * 方法名
     */
    private String name;
    /**
     * 方法参数
     */
    private Object[] args;

    /**
     * 方法返回值
     */
    private Object value;

    /**
     * 原始模板 {m1{#p1}}
     */
    private String sourceTemplate;

    /**
     * 原始的方法参数spel表达式（通过sourceTemplate解析出来的）
     */
    private List<String> argsSpelExpression;

    /**
     * 是否是前置执行
     */
    private boolean beforeMethod;


}
