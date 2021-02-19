package com.zhiyong.gateway.common.enums;

/**
 * @ClassName ErrorCode
 * @Description: 网关统一错误码
 * @Author 毛军锐
 * @Date 2020/11/24 下午9:07
 **/
public enum ErrorCode {
    ISP_SYSTEM_ERROR(500, "isp.error:system-error", "系统开小差了"),
    ISP_CONFIG_ERROR(500, "isp.error:config-error", "系统配置错误"),
    ISP_API_PROVIDER_NOT_FOUND(500, "isp.error:api-provider-not-found", "找不到API服务提供方"),
    ISP_API_STATE_INVALID(5001, "isp.error:api-state-invalid", "api状态异常"),
    ISP_API_SIGN_FAIL(5001, "isp.error:api-sign-fail", "计算签名失败"),
    ISV_ILLEGAL_ARGUMENT(4001, "isv.error:illegal-argument", "非法请求参数"),
    ISV_ARGUMENT_TYPE_ERROR(4001, "isv.error:argument-type-error", "请求参数类型错误"),
    ISV_NOT_SUPPORT_REQ_TYPE(4001, "isv.error:not-support-request-type", "不支持的请求类型"),
    ISV_LOGIN_FAIL(5002, "isv.error:login-fail", "登录失败"),
    ISV_SIGN_NO_PASS(5006, "isv.error:sign-no-pass", "签名不正确"),
    ISV_NO_AUTH(401, "isv.error:no-auth", "无权访问,未登录或登录已过期"),
    ISV_NO_PERMISSION(1110, "isv.error:no-auth", "无权访问该资源信息"),
    ISV_TOKEN_TENANT_EMPTY(1100, "isv.error:no-session", "用户还没有绑定任何租户"),
    ;

    private int state;
    private String code;
    private String desc;

    ErrorCode(int state, String code, String desc) {
        this.state = state;
        this.code = code;
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    
    
    public static ErrorCode getErrorByCode(String code) {
        if (code == null) {
            return null;
        }
        ErrorCode[] values = ErrorCode.values();
        for (ErrorCode item : values) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

}
