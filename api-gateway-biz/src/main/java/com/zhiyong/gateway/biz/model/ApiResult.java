package com.zhiyong.gateway.biz.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ApiResponse
 * @Description: 网关统一响应对象
 * @Author 毛军锐
 * @Date 2020/11/24 下午8:39
 **/
@Data
@Builder
public class ApiResult implements Serializable {
    private static final long serialVersionUID = -4302529587307861184L;

    /**
     * 请求唯一ID
     */
    private String requestId;

    /**
     * 响应状态码
     */
    private Integer code = 200;

    /**
     * 响应状态描述信息
     */
    private String msg = "OK";

    /**
     * 响应对象信息
     */
    private Object data;

    /**
     * 构建异常对象
     *
     * @param code
     * @param msg
     * @return
     */
    public static ApiResult buildErrorResult(int code, String msg) {
        ApiResult apiResult = ApiResult.builder().build();
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
