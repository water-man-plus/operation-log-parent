package com.water.ad.operation.log.test;

import com.google.common.collect.Lists;
import com.water.ad.operation.log.aop.LogRecordPointcut;
import com.water.ad.operation.log.core.DefaultOperatorGetServiceImpl;
import com.water.ad.operation.log.core.OperatorGetService;
import com.water.ad.operation.log.core.diff.DefaultObjectDiffHandler;
import com.water.ad.operation.log.core.diff.ObjectDiffHandler;
import com.water.ad.operation.log.core.function.*;
import com.water.ad.operation.log.core.parse.LogRecordExpressionEvaluator;
import com.water.ad.operation.log.core.parse.LogRecordExpressionParser;
import com.water.ad.operation.log.core.record.LogRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @author yyq
 * @create 2022-02-22
 **/
@Configuration
@Slf4j
public class LogRecordPointcutTestConfig {


    @Bean
    public LogRecordPointcutTest.UserService testService() {
        return new LogRecordPointcutTest.UserService();
    }

    @Bean
    public LogRecordPointcut logRecordInterceptor(LogRecordExpressionEvaluator logRecordExpressionEvaluator,
                                                  LogRecordService logRecordService,
                                                  OperatorGetService operatorGetService,
                                                  LogRecordExpressionParser logRecordExpressionParser) {
        return new LogRecordPointcut(logRecordExpressionEvaluator, logRecordService, operatorGetService, logRecordExpressionParser);
    }

    @Bean
    @ConditionalOnMissingBean(LogRecordExpressionEvaluator.class)
    public LogRecordExpressionEvaluator logRecordValueParser(LogRecordExpressionParser logRecordExpressionParser, FunctionService functionService) {
        return new LogRecordExpressionEvaluator(logRecordExpressionParser);
    }

    @Bean
    @ConditionalOnMissingBean(OperatorGetService.class)
    public OperatorGetService operatorGetService() {
        return new DefaultOperatorGetServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(FunctionService.class)
    @Primary
    public FunctionService functionService(ParseFunctionFactory parseFunctionFactory, BeanFactory beanFactory) {
        DefaultFunctionServiceImpl defaultFunctionService = new DefaultFunctionServiceImpl(parseFunctionFactory);
        SpringContextBeanFunctionServiceImpl contextFunctionService = new SpringContextBeanFunctionServiceImpl(beanFactory);
        TargetObjectFunctionServiceImpl objectFunctionService = new TargetObjectFunctionServiceImpl();
        return new PrioritizedFunctionServiceImpl(Lists.newArrayList(defaultFunctionService, contextFunctionService, objectFunctionService));
    }


    @Bean
    public ParseFunctionFactory parseFunctionFactory(@Autowired(required = false) List<ParseFunction> parseFunctions) {
        return new ParseFunctionFactory(parseFunctions);
    }

    @Bean
    @ConditionalOnMissingBean(LogRecordService.class)
    public LogRecordService recordService() {
        return new LogRecordPointcutTest.TestLogRecordRecordService();
    }

    @Bean
    @ConditionalOnMissingBean(ObjectDiffHandler.class)
    public ObjectDiffHandler objectDiffHandler() {
        return new DefaultObjectDiffHandler();
    }

    @Bean
    public LogRecordExpressionParser logRecordExpressionParser(FunctionService functionService,
                                                               ObjectDiffHandler objectDiffHandler) {
        return new LogRecordExpressionParser(functionService, objectDiffHandler);
    }

}
