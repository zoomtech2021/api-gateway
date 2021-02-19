package com.zhiyong.gateway.biz.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ApiParamsDto
 * @Description: api请求参数对象
 * @Author 毛军锐
 * @Date 2020/11/25 上午10:49
 **/
@Data
@Builder
public class ApiParams {
    /**
     * 参数类型列表
     */
    private String[] paramTypes;

    /**
     * 参数值列表
     */
    private Object[] paramValues;

    /**
     * 签名参数集合
     */
    private Map<String, String> signParams;
}
