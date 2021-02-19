package com.zhiyong.gateway.common.model;

import java.io.Serializable;

/**
 * @author tumingjian 创建时间: 2020-11-18 17:23 功能说明:
 */
public interface Response<T> extends Serializable {
    /**
     * 响应code码
     * @return
     */
    int getCode();

    /**
     * 响应消息.一般在返回错误码时才关注消息
     * @return
     */
    String getMsg();

    /**
     * 结果数据
     * @return
     */
    T getData();
}
