package com.zhiyong.gateway.biz.model;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ServiceRequest
 * @Description: 远程服务调用请求对象
 * @Author 毛军锐
 * @Date 2020/11/25 下午7:20
 **/
@Data
@Builder
public class ServiceRequest {

    /**
     * 接口类名（完整路径）
     */
    private String interfaceName;

    /**
     * 调用方法名
     */
    private String method;

    /**
     * 调用接口版本
     */
    private String version;

    /**
     * 接口调用超时时间
     */
    private int timeout;

    /**
     * 接口方法参数类型列表
     */
    private String[] paramTypes;

    /**
     * 接口方法参数值列表
     */
    private Object[] paramValues;
}
