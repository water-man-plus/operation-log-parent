package com.water.ad.operation.log.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yyq
 * @create 2022-02-17
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogRecord {

    private Date time;

    private String operatorId;

    private String bizNo;

    private String detail;

    /**
     * 类别
     */
    private String category;
}
