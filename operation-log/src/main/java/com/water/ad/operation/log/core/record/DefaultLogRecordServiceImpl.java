package com.water.ad.operation.log.core.record;

import com.water.ad.operation.log.model.LogRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yyq
 * @create 2022-02-17
 **/
@Slf4j
public class DefaultLogRecordServiceImpl implements LogRecordService {

    @Override
    public void record(LogRecord logRecord) {

        log.info("【logRecord】log={}", logRecord);
    }
}
