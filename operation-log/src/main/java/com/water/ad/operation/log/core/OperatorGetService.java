package com.water.ad.operation.log.core;

import lombok.*;

/**
 * @author yyq
 * @create 2022-02-17
 **/
public interface OperatorGetService {


    /**
     * get operator user
     *
     * @return operator user
     */
    Operator getUser();


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class Operator {

        private String id;

        private String name;
    }
}
