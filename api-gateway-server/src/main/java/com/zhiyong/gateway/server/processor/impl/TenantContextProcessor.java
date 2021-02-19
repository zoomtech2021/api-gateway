package com.zhiyong.gateway.server.processor.impl;

import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import org.springframework.stereotype.Component;

/**
 * @ClassName TenantContextProcessor
 * @Description: 租户上下文处理器
 * @Author 毛军锐
 * @Date 2020/12/4 下午5:24
 **/
@Component
public class TenantContextProcessor implements ApiProcessor {

    @Override
    public String getName() {
        return TenantContextProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {

    }
}
