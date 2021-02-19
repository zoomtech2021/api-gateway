package com.zhiyong.gateway.server.processor.impl;

import com.zhiyong.gateway.biz.model.ServiceRequest;
import com.zhiyong.gateway.biz.service.RpcProxyService;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @ClassName ServiceInvokeProcessor
 * @Description: 远程服务调用处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:31
 **/
@Component
public class ServiceInvokeProcessor implements ApiProcessor {

    @Resource
    private RpcProxyService rpcProxyService;

    @Override
    public String getName() {
        return ServiceInvokeProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        ServiceRequest serviceRequest = ServiceRequest.builder()
                .interfaceName(context.getApiCfg().getRpcInterface())
                .method(context.getApiCfg().getRpcMethod())
                .timeout(context.getApiCfg().getTimeout())
                .version(context.getApiCfg().getRpcVersion())
                .paramTypes(context.getApiParams().getParamTypes())
                .paramValues(context.getApiParams().getParamValues())
                .build();
        Object result = rpcProxyService.invoke(serviceRequest);
        context.setApiResult(result);
    }
}
