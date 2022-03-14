package com.water.ad.operation.log.starter.annotation;


import com.water.ad.operation.log.starter.config.LogRecordAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yyq
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogRecordAutoConfiguration.class)
public @interface EnableLogRecord {

}
