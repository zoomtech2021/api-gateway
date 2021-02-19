package com.zhiyong.gateway.common.exception;

import com.zhiyong.gateway.common.enums.ErrorCode;

/**
 * @author tumingjian 创建时间: 2020-12-17 14:32 功能说明:
 */
public class BizSessionException extends GatewayException {
    public BizSessionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BizSessionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BizSessionException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public BizSessionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
