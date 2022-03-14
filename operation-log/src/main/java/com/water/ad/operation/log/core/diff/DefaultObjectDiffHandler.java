package com.water.ad.operation.log.core.diff;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.water.ad.operation.log.annotation.OpLogField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.expression.EvaluationException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yyq
 * @create 2022-02-23
 **/
@Slf4j
public class DefaultObjectDiffHandler extends AbstractObjectDiffHandler {

    /**
     * 4 space
     */
    private final static String INDENT_APPENDER = "    ";

    @Override
    public String diff(Object pre, Object post, List<String> specificFieldList) {

        Assert.notNull(pre, "diff pre Object can not be null");
        Assert.notNull(post, "diff post Object can not be null");

        if (pre.getClass() != post.getClass()) {
            throw new EvaluationException(String.format("diff object must be  the same type pre [%s] , post [%s]", pre.getClass(), post.getClass()));
        }
        String indent = "";
        StringBuilder sb = new StringBuilder();
        OpLogField opLogFieldAnnotation;
        Field[] fields;
        fields = pre.getClass().getDeclaredFields();
        List<Field> fieldList;
        if (CollectionUtils.isNotEmpty(specificFieldList)) {
            fieldList = new ArrayList<Field>();
            for (Field field : fields) {
                if (specificFieldList.contains(field.getName())) {
                    fieldList.add(field);
                }
            }
            if (CollectionUtils.isEmpty(specificFieldList)) {
                throw new EvaluationException("specified field list not found when diff " + JSON.toJSONString(specificFieldList));
            }
        } else {
            fieldList = Arrays.asList(fields);
        }
        Class modelClass = pre.getClass();
        String fieldName;
        String fieldMapping;
        String dateFormat;
        String decimalFormat;
        JSONObject fieldMap;
        Object valuePre;
        Object valuePost;
        indent = indent + INDENT_APPENDER;
        try {
            for (Field field : fieldList) {
                boolean hasAnnotation = field.isAnnotationPresent(OpLogField.class);
                if (!hasAnnotation) {
                    log.warn("field without OpLogField annotation,skip diff field name {}", field.getName());
                    continue;
                }
                opLogFieldAnnotation = field.getAnnotation(OpLogField.class);
                boolean ignore = opLogFieldAnnotation.ignore();
                if (ignore) {
                    //skip the ignore fields
                    continue;
                }
                fieldName = opLogFieldAnnotation.fieldName();
                if (StringUtils.isEmpty(fieldName)) {
                    fieldName = field.getName();
                }

                dateFormat = opLogFieldAnnotation.dateFormat();
                decimalFormat = opLogFieldAnnotation.decimalFormat();
                fieldMapping = opLogFieldAnnotation.fieldMapping();

                valuePre = FieldUtils.readDeclaredField(pre, field.getName(), true);
                valuePost = FieldUtils.readDeclaredField(post, field.getName(), true);

                if (valuePre == null && valuePost == null) {
                    continue;
                }
                //OpLogModel should have it's own equals method.
                if (valuePre != null && valuePost != null) {
                    if (objectEquals(valuePre, valuePost)) {
                        continue;
                    }
                }
                valuePre = formatDateField(modelClass, dateFormat, valuePre, field);
                valuePost = formatDateField(modelClass, dateFormat, valuePost, field);

                valuePre = formatDecimal(modelClass, decimalFormat, valuePre, field);
                valuePost = formatDecimal(modelClass, decimalFormat, valuePost, field);

                fieldMap = parseFieldMapping(modelClass, fieldMapping, field);

                valuePre = doFieldMapping(fieldMap, valuePre);
                valuePost = doFieldMapping(fieldMap, valuePost);

                Class subModelClz = null;
                if (valuePre != null) {
                    subModelClz = valuePre.getClass();
                }
                if (valuePost != null) {
                    subModelClz = valuePost.getClass();
                }
                sb.append(fieldName).append(": ")
                        .append(valuePre).append(" --> ").append(valuePost).append("\n");
            }
        } catch (Exception e) {
            throw new EvaluationException("diff Object error " + e.getMessage(), e);
        }
        if (sb.length() > 1 && sb.toString().endsWith("\n")) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }
}
