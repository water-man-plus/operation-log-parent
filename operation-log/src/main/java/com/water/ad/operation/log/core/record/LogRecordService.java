package com.water.ad.operation.log.core.record;

import com.water.ad.operation.log.model.LogRecord;

/**
 * @author yyq
 * @create 2022-02-17
 **/
public interface LogRecordService {

    /**
     * 保存log
     *
     * @param logRecord 日志实体
     */
    void record(LogRecord logRecord);
}
