package com.zhiyong.gateway.server.processor.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import org.springframework.stereotype.Component;

/**
 * @ClassName FlowControlProcessor
 * @Description: 流控处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:29
 **/
@Component
public class FlowControlProcessor implements ApiProcessor {

    @Override
    public String getName() {
        return FlowControlProcessor.class.getName();
    }

    /**
     * 流控处理，流控规则设置请参考 FlowControlRuleCache
     * @param context
     * @throws GatewayException
     */
    @Override
    public void run(ApiContext context) throws GatewayException {
        Entry apiCallLimit = null;

        try {
            String method = context.getApiRequest().getMethod();
            String version = context.getApiRequest().getVersion();
            String resource = method + ":" + version;
            apiCallLimit = SphU.entry(resource);
        } catch (BlockException e1) {
            throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "API被限流了");
        } finally {
            if (apiCallLimit != null) {
                apiCallLimit.exit();
            }
        }
    }
}
