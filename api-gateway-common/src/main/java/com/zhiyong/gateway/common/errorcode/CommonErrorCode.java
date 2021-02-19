package com.zhiyong.gateway.common.errorcode;

public enum CommonErrorCode implements ErrorCode {
    /**
     * 成功返回码
     */
    CODE_SUCCESS(200, "OK"),

    /**
     * 返回内容为空
     */
    CODE_CONTENT_EMPTY(204, "OK"),

    /**
     * 访问被拒绝
     */
    CODE_REJECT(403, "未授权"),

    /**
     * 无权限访问
     */
    CODE_NO_AUTH(401, "未认证"),

    /**
     * 请求方法错误
     */
    CODE_NOT_EXIST(404, "请求方法错误"),

    /**
     * 请求版本错误
     */
    CODE_VERSION_ERROR(406, "请求版本错误"),

    /**
     * 获取锁错误
     */
    CODE_GET_LOCK_ERROR(423, "获取锁失败"),

    /**
     * 服务器错误
     */
    CODE_SERVER_ERROR(600, "服务器错误"),
    /**
     * 服务超时
     */
    CODE_SERVER_TIMEOUT(601, "服务超时"),
    /**
     * 其他业务码定义
     */
    CODE_COMMON_ERROR(800, "默认异常"),
    /**
     * 缺少必填参数
     */
    CODE_PARAM_NULL(801, "缺少必填参数"),

    /**
     * 请求参数错误
     */
    CODE_PARAM_ERROR(802, "请求参数不合法"),

    /**
     * 租户ID为空异常
     */
    TENANT_ID_EMPTY_ERROR(998, "租户ID为空"),

    /**
     * 租户上下文为空异常
     */
    TENANT_CONTEXT_EMPTY_ERROR(999, "租户上下文为空");

    private int code;
    private String msg;

    CommonErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
