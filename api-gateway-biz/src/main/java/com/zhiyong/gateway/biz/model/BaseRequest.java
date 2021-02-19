package com.zhiyong.gateway.biz.model;

import lombok.Data;

/**
 * @ClassName BaseRequest
 * @Description: 基础请求信息
 * @Author 毛军锐
 * @Date 2020/11/25 上午11:42
 **/
@Data
public class BaseRequest {

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 请求域名
     */
    private String host;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求类型
     */
    private String reqMethod;

    /**
     * 请求referer
     */
    private String referer;

    /**
     * 请求contentType
     */
    private String contentType;

    /**
     * 请求客户端
     */
    private String userAgent;

    /**
     * 请求参数
     */
    private String queryStr;

    /**
     * 移动端设备ID
     */
    private String deviceId;

    /**
     * 移动端操作系统
     */
    private String os;

    /**
     * 移动端操作系统版本
     */
    private String osVersion;
}
