package com.water.ad.operation.log.core.diff;

import java.util.List;

/**
 * @author yyq
 * @create 2022-02-23
 **/
public interface ObjectDiffHandler {

    /**
     * 对象diff
     *
     * @param pre               pre object
     * @param post              post Object
     * @param specificFieldList 只对比特定的field
     * @return diff差异结果
     */
    String diff(Object pre, Object post, List<String> specificFieldList);
}
