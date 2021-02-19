package com.zhiyong.gateway.server.processor;

import com.zhiyong.gateway.biz.model.ApiResult;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.impl.ApiParamParseProcessor;
import com.zhiyong.gateway.server.processor.impl.FlowControlProcessor;
import com.zhiyong.gateway.server.processor.impl.ResultConverterProcessor;
import com.zhiyong.gateway.server.processor.impl.ServiceInvokeProcessor;
import com.zhiyong.gateway.server.processor.impl.TenantContextProcessor;
import com.zhiyong.gateway.server.processor.impl.callback.CallbackConfigLoadProcessor;
import com.zhiyong.gateway.server.processor.impl.callback.CallbackReqParseProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName CallbackProcessorChain
 * @Description: 回调API处理链
 * @Author 毛军锐
 * @Date 2021/1/14 上午11:24
 **/
@Component
public class CallbackProcessorChain implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackProcessorChain.class);
    private Map<String, ApiProcessor> processors = new HashMap<>();
    private List<String> registerProcessor = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ApiProcessor> beanMap = applicationContext.getBeansOfType(ApiProcessor.class);
        for (ApiProcessor processor : beanMap.values()) {
            processors.put(processor.getName(), processor);
        }
    }

    @PostConstruct
    private void register() {
        // 参数解析
        registerProcessor.add(CallbackReqParseProcessor.class.getName());
        registerProcessor.add(CallbackConfigLoadProcessor.class.getName());
        registerProcessor.add(ApiParamParseProcessor.class.getName());
        // 验证+执行
        registerProcessor.add(FlowControlProcessor.class.getName());
        registerProcessor.add(TenantContextProcessor.class.getName());
        registerProcessor.add(ServiceInvokeProcessor.class.getName());
        registerProcessor.add(ResultConverterProcessor.class.getName());
        LOGGER.info("====register gateway callback processor completed!");
    }

    /**
     * 回调处理器执行器
     *
     * @param context
     * @return
     */
    public ApiResult execute(ApiContext context) {
        for (String name : registerProcessor) {
            ApiProcessor processor = processors.get(name);
            if (processor == null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR);
            }
            processor.run(context);
        }
        ApiResult apiResult = (ApiResult) context.getApiResult();
        apiResult.setRequestId(context.getRequestId());
        return apiResult;
    }
}
