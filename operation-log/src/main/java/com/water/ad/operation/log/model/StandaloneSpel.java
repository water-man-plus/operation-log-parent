package com.water.ad.operation.log.model;

import lombok.*;

/**
 * 单独的表达式
 *
 * @author yyq
 * @create 2022-02-22
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandaloneSpel {


    private String template;

    private Object value;
}
