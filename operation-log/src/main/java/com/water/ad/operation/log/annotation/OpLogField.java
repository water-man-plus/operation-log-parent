/**
 * Copyright 2020 Jasper J B Deng(djbing85@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.water.ad.operation.log.annotation;


import java.lang.annotation.*;

/**
 * @author djbing85@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OpLogField {


    /**
     * field name in a human-friendly way,
     * default/empty field name in a BO means to generate oplog with it's raw field name
     *
     * @return field name
     */
    String fieldName() default "";


    /**
     * JSON string mapper<p>
     * A typical value should look like: <p>
     * ---- {"0": "disabled", "1": "enabled"} ----<p>
     * Map the field values like [0/1] to a human-readable string like [enabled/disabled] <p>
     * Oplog intercepter will take the field value as key,
     * "translate" it to the mapped value while generating the oplog automatically<p>
     * Default or empty means field value will be used directly<p>
     * A malformed JSON string will result to a false translate: that is, to use raw value.
     *
     * @return Default "{}"
     */
    String fieldMapping() default "{}";

    /**
     * Specify if the field should be ignored when generate oplog detail<p>
     * An ignore field will be skip when compare differences between two BO object<p>
     *
     * @return Default false
     */
    boolean ignore() default false;


    /**
     * Date format will only apply on field type list below: <p>
     * java.util.Date<p>
     * java.util.Calendar<p>
     * java.time.LocalDate<p>
     * java.time.LocalTime<p>
     * java.time.LocalDateTime<p>
     * Invalid date format will result to a format failure,<p>
     *
     * @return Default "yyyy-MM-dd HH:mm:ss"
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * A valid decimalFormat should looks like: #,###.##<p>
     * Will try to format Double/Float/Long/Integer/BigDecimal fields <p>
     * Empty decimalFormat will be ignore<p>
     * <p>
     * For example: #,###.## <p>
     * Double d = 554545.4545454; <p>
     * Formatted String: 554,545.45; <p>
     * Long l = 1234567890;<p>
     * Formatted String: 1,234,567,890<p>
     *
     * @return Default ""
     */
    String decimalFormat() default "";

}
