package com.zhiyong.gateway.common.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName PageRequest
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/9 下午3:37
 **/
@Data
public class PageRequest {
    private int page = 1;
    private int rows = 10;
    private String sort;
    private String order = " asc ";

    /**
     * 获取排序字段
     *
     * @return
     */
    public String getSortColumn() {
        if (StringUtils.isNotBlank(sort)) {
            return sort.replaceAll("[A-Z]", "_$0").toLowerCase();
        }
        return sort;
    }
}
