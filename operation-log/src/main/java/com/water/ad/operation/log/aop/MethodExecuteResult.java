package com.water.ad.operation.log.aop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author yyq
 * @create 2022-02-17
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MethodExecuteResult {

    private boolean success = true;
    private Throwable throwable;
    private String errorMsg;
}
