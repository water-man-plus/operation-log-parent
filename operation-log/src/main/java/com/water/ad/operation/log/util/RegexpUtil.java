package com.water.ad.operation.log.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yyq
 */
public class RegexpUtil {

    /**
     * 判断给定字符串中是否含有制定字符串
     *
     * @param src
     * @param reg
     * @return
     */
    public static boolean hasStr(String src, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(src);
        return m.find();
    }
}
