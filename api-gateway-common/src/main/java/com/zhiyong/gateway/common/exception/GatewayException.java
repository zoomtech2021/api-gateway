package com.zhiyong.gateway.common.exception;

import com.zhiyong.gateway.common.enums.ErrorCode;

/**
 * @ClassName GatewayException
 * @Description: 统一网关异常
 * @Author 毛军锐
 * @Date 2020/11/24 下午8:44
 **/
public class GatewayException extends RuntimeException {
    private ErrorCode errorCode;

    public GatewayException(ErrorCode errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode;
    }

    public GatewayException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GatewayException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GatewayException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
