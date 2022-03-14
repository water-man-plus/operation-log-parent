package com.water.ad.operation.log.core;

/**
 * @author yyq
 * @create 2022-02-17
 **/
public class DefaultOperatorGetServiceImpl implements OperatorGetService {

    @Override
    public Operator getUser() {
        return Operator.builder().id("-1").name("unknown").build();
    }
}
