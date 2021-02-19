package com.zhiyong.gateway.biz.service;

import com.zhiyong.gateway.biz.model.ServiceRequest;
import com.zhiyong.gateway.common.exception.GatewayException;

/**
 * @ClassName RpcProxyService
 * @Description: rpc远程调用代理接口
 * @Author 毛军锐
 * @Date 2020/11/24 下午8:18
 **/
public interface RpcProxyService {

    /**
     * 远程方法调用
     *
     * @param request
     * @return
     * @throws GatewayException
     */
    Object invoke(ServiceRequest request) throws GatewayException;
}
