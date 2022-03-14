package com.water.ad.operation.log.core.function;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yyq
 * @create 2022-02-17
 **/
public class ParseFunctionFactory {

    private Map<String, ParseFunction> allFunctionMap = new HashMap<>(16);

    public ParseFunctionFactory(List<ParseFunction> parseFunctions) {
        if (CollectionUtils.isEmpty(parseFunctions)) {
            return;
        }
        for (ParseFunction parseFunction : parseFunctions) {
            if (StringUtils.isEmpty(parseFunction.functionName())) {
                continue;
            }
            allFunctionMap.put(parseFunction.functionName(), parseFunction);
        }
    }

    public ParseFunction getFunction(String functionName) {
        return allFunctionMap.get(functionName);
    }
}
