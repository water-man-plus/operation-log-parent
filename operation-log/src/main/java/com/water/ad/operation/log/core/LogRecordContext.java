package com.water.ad.operation.log.core;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author yyq
 * @create 2022-02-17
 **/
@Getter
@Setter
public class LogRecordContext {

    /**
     * 使用栈结构，解决嵌套操作记录问题
     */
    private static final InheritableThreadLocal<Stack<Map<String, Object>>> VARIABLE_MAP_STACK = new InheritableThreadLocal<Stack<Map<String, Object>>>() {
        @Override
        protected Stack<Map<String, Object>> initialValue() {
            Stack<Map<String, Object>> stack = new Stack<>();
            stack.push(new HashMap<>(16));
            return stack;
        }
    };

    /**
     * 自定义操作内容
     */
    public static final String CUSTOM_LOG_DETAIL_KEY = "$$CUSTOM_LOG_DETAIL";

    public static final String CUSTOM_LOG_APPEND_DETAIL_KEY = "$$CUSTOM_LOG_APPEND_DETAIL";

    public static final String TEMPLATE_CONTEXT_KEY = "$$TEMPLATE_CONTEXT";


    public static void putVariable(String key, Object value) {
        Map<String, Object> map = getVariables();
        map.put(key, value);
    }

    public static Map<String, Object> getVariables() {
        if (VARIABLE_MAP_STACK.get().empty()) {
            //压入一个新的
            VARIABLE_MAP_STACK.get().push(new HashMap<>(16));
        }
        return VARIABLE_MAP_STACK.get().peek();
    }

    /**
     * 用户直接定义操作明细
     *
     * @param detail detail
     */
    public static void putLogDetail(String detail) {
        putVariable(CUSTOM_LOG_DETAIL_KEY, detail);
    }

    /**
     * 用户在切面解析的基础上，在原内容后面附加内容
     *
     * @param detailAppend detailAppend
     */
    public static void putLogDetailAppend(String detailAppend) {
        putVariable(CUSTOM_LOG_APPEND_DETAIL_KEY, detailAppend);
    }

    /**
     * 出栈释放
     */
    public static void clear() {
        if (!VARIABLE_MAP_STACK.get().isEmpty()) {
            VARIABLE_MAP_STACK.get().pop();
        }
    }
}
