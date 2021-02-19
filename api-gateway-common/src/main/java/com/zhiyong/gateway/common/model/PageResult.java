package com.zhiyong.gateway.common.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName PageResult
 * @Description: 分页对象
 * @Author 毛军锐
 * @Date 2020/11/26 下午8:01
 **/
@Data
@Builder
public class PageResult {
    private long total;
    private List rows;
    private int pageNum;
    private int pageSize;
}
