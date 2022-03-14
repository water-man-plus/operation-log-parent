package com.water.ad.operation.log.core.diff;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yyq
 * @create 2022-02-23
 **/
@Slf4j
public abstract class AbstractObjectDiffHandler implements ObjectDiffHandler {

    protected Object formatDateField(Class modelClass, String dateFormat, Object value, Field field) {
        if (StringUtils.isEmpty(dateFormat)) {
            return value;
        }
        if (value == null) {
            return value;
        }
        DateFormat sdf;
        if (value instanceof Date) {
            try {
                sdf = new SimpleDateFormat(dateFormat);
                value = sdf.format((Date) value);
            } catch (Exception e) {
                log.error("error format date {} with given dateFormat: {} in class: {}, field: {}", value, dateFormat, modelClass, field.getName(), e);
            }
        }
        if (value instanceof Calendar) {
            try {
                sdf = new SimpleDateFormat(dateFormat);
                value = sdf.format(((Calendar) value).getTime());
            } catch (Exception e) {
                log.error("error format Calendar {} with given dateFormat: {} in class: {}, field: {}", value, dateFormat, modelClass, field.getName(), e);

            }
        }
        if (value instanceof LocalDate) {
            try {
                LocalDate tempLocalDate = (LocalDate) value;
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(dateFormat);
                value = formatter1.format(tempLocalDate);
            } catch (Exception e) {
                log.error("error format LocalDate {} with given dateFormat: {} in class: {}, field: {}", value, dateFormat, modelClass, field.getName(), e);
            }
        }
        if (value instanceof LocalTime) {
            try {
                LocalTime tempLocalTime = (LocalTime) value;
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(dateFormat);
                value = formatter1.format(tempLocalTime);
            } catch (Exception e) {
                log.error("error format LocalTime {} with given dateFormat: {} in class: {}, field: {}", value, dateFormat, modelClass, field.getName(), e);
            }
        }
        if (value instanceof LocalDateTime) {
            try {
                LocalDateTime tempLocalDateTime = (LocalDateTime) value;
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(dateFormat);
                value = formatter1.format(tempLocalDateTime);
            } catch (Exception e) {
                log.error("error format LocalDateTime {} with given dateFormat: {} in class: {}, field: {}", value, dateFormat, modelClass, field.getName(), e);
            }
        }
        return value;
    }

    protected JSONObject parseFieldMapping(Class modelClass, String fieldMapping, Field field) {
        JSONObject fieldMap;
        try {
            if (!StringUtils.isEmpty(fieldMapping)) {
                fieldMap = JSONObject.parseObject(fieldMapping);
            } else {
                fieldMap = new JSONObject();
            }
        } catch (Exception e) {
            log.error("fieldMapping found wrong json format: {} in class: {}, field: {}", fieldMapping, modelClass, field.getName(), e);
            fieldMap = new JSONObject();
        }
        return fieldMap;
    }

    protected Object doFieldMapping(JSONObject fieldMap, Object value) {
        if (fieldMap != null && fieldMap.size() > 0) {
            Object mapVal = fieldMap.get(value);
            if (mapVal != null) {
                value = mapVal;
            }
        }
        return value;
    }

    protected Object formatDecimal(Class modelClass, String decimalFormat, Object value, Field field) {
        if (!StringUtils.isEmpty(decimalFormat)) {
            if (value instanceof BigDecimal || value instanceof Double ||
                    value instanceof Float || value instanceof Long ||
                    value instanceof Integer) {
                try {
                    DecimalFormat df = new DecimalFormat(decimalFormat);
                    value = df.format(value);
                } catch (Exception e) {
                    log.error("error format digit with given decimalFormat:{} in class: {}, field: {}", decimalFormat, modelClass, field.getName(), e);
                }
            }
        }
        return value;
    }

    protected boolean objectEquals(Object valuePre, Object valuePost) {
        if (valuePre instanceof BigDecimal && valuePost instanceof BigDecimal) {
            return ((BigDecimal) valuePre).stripTrailingZeros().equals(((BigDecimal) valuePost).stripTrailingZeros());
        }
        return valuePre.equals(valuePost);
    }
}
