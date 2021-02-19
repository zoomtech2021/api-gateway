package com.zhiyong.gateway.common.model;

import com.zhiyong.gateway.common.errorcode.CommonErrorCode;
import com.zhiyong.gateway.common.errorcode.ErrorCode;

public class ApiResponse<T> implements Response<T> {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private T data;


    public ApiResponse() {

    }

    /**
     * 构建空返回对象
     *
     * @return
     */
    public static <T> ApiResponse<T> buildEmptyResponse() {
        return buildResponse(CommonErrorCode.CODE_CONTENT_EMPTY.getCode(), null);
    }

    /**
     * 构建成功返回数据对象
     *
     * @param data 返回数据内容
     * @return
     */
    public static <T> ApiResponse<T> buildSuccessResponse(T data) {
        return buildResponse(CommonErrorCode.CODE_SUCCESS.getCode(), data, null);
    }

    /**
     * 构建常规错误返回对象
     *
     * @param msg
     * @return
     */
    public static <T> ApiResponse<T> buildCommonErrorResponse(String msg) {
        return buildResponse(CommonErrorCode.CODE_COMMON_ERROR.getCode(), msg);
    }

    /**
     * 构建自定义CODE码对象
     *
     * @param code
     * @param data
     * @param msg
     * @return
     */
    public static <T> ApiResponse<T> buildResponse(int code, T data, String msg) {
        return buildResponse(code, msg, data);
    }

    /**
     * 构建CODE码和提示信息对象
     * 
     * @param errorCode
     * @return
     */
    public static <T> ApiResponse<T> buildResponse(ErrorCode errorCode) {
        return buildResponse(errorCode.getCode(), errorCode.getMsg(), (T) null);
    }

    /**
     * 构建用户自定义CODE码和提示信息对象
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> ApiResponse<T> buildResponse(int code, String msg) {
        return buildResponse(code, msg, (T) null);
    }

    /**
     * 构建自定义CODE码对象
     *
     * @param code
     * @param data
     * @param msg
     * @return
     */
    public static <T> ApiResponse<T> buildResponse(int code, String msg, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setData(data);
        response.setMsg(msg);
        return response;
    }



    /**
     * 是否执行成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return CommonErrorCode.CODE_SUCCESS.getCode() == this.code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ApiResponse [code=")
                .append(code)
                .append(", msg=")
                .append(msg)
                .append(", data=")
                .append(data)
                .append("]");
        return builder.toString();
    }
}
