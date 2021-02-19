package com.zhiyong.gateway.server.processor;

import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;

/**
 * @ClassName ApiProcessor
 * @Description: API处理器
 * @Author 毛军锐
 * @Date 2020/11/25 上午11:37
 **/
public interface ApiProcessor {

    /**
     * 处理器名称
     */
    String getName();

    /**
     * 处理操作
     * @param context
     */
    void run(ApiContext context) throws GatewayException;
}
